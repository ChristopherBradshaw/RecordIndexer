package client.centerpanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import shared.communication.DownloadBatchOutput;
import shared.model.Batch;
import shared.model.Field;
import shared.model.Project;
import client.Client;
import client.communication.ClientCommunicator;
import client.frames.IndexerFrame;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel {
	private static Image NULL_IMAGE = new BufferedImage(10, 10,
			BufferedImage.TYPE_INT_ARGB);

	private int w_translateX;
	private int w_translateY;
	private double scale;

	private boolean dragging;
	private int w_dragStartX;
	private int w_dragStartY;
	private int w_dragStartTranslateX;
	private int w_dragStartTranslateY;
	private AffineTransform dragTransform;

	private ArrayList<DrawingShape> shapes;

	private ArrayList<DrawingListener> listeners;

	private Image image;
	private BufferedImage imageBuffered;

	private boolean hasHighlights;
	private boolean isInverted;

	private int selectedRow;
	private int selectedCol;

	private static final double DRAG_SPEED = .05;
	private static final double MIN_ZOOM = .3;
	private static final double MAX_ZOOM = 1.25;

	private static DownloadBatchOutput imageData;
	private boolean hasAssignedBatch;


	private int imageX;
	private int imageY;

	private IndexerFrame parent;

	public ImagePanel(int width, int height, IndexerFrame parent) {
		super();
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.GRAY);

		this.parent = parent;
		this.hasHighlights = true;
		this.image = null;
		this.scale = .5;
		this.selectedRow = 0;
		this.selectedCol = 0;

		w_translateX = 0;
		w_translateY = 0;
		scale = 1.0;

		initDrag();

		shapes = new ArrayList<DrawingShape>();

		listeners = new ArrayList<DrawingListener>();

		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
		this.addComponentListener(componentAdapter);
	}

	private void initDrag() {
		dragging = false;
		w_dragStartX = 0;
		w_dragStartY = 0;
		w_dragStartTranslateX = 0;
		w_dragStartTranslateY = 0;
		dragTransform = null;
	}

	public boolean isInverted() {
		return isInverted;
	}

	public boolean hasHighlights() {
		return hasHighlights;
	}

	public static DownloadBatchOutput getBatchData() {
		return imageData;
	}

	public void setRow(int row) {
		selectedRow = row;

		repaint();
	}

	public void setCol(int col) {
		selectedCol = col;
		repaint();
	}

	public void setTranslation(int w_newTranslateX, int w_newTranslateY) {
		w_translateX = w_newTranslateX;
		w_translateY = w_newTranslateY;
		this.repaint();
	}

	public void addDrawingListener(DrawingListener listener) {
		listeners.add(listener);
	}

	private void notifyTranslationChanged(int w_newTranslateX,
			int w_newTranslateY) {
		for (DrawingListener listener : listeners) {
			listener.translationChanged(w_newTranslateX, w_newTranslateY);
		}
	}

	public void setTranslateX(int x) {
		w_translateX = x;
		this.repaint();
	}

	public void setTranslateY(int y) {
		w_translateY = y;
		this.repaint();
	}

	public int getTranslateX() {
		return w_translateX;
	}

	public int getTranslateY() {
		return w_translateY;
	}

	public double getScale() {
		return scale;
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		drawBackground(g2);

		if(image == null)
			return;
		
//		g2.scale(scale, scale);
//		g2.translate(w_translateX, w_translateY);
		g2.translate(this.getWidth() / 2.0, this.getHeight() / 2.0);
		g2.scale(scale, scale);
		g2.translate( -image.getWidth(null) / 2.0 + w_translateX,
		 -image.getHeight(null) / 2.0 + w_translateY );
		drawShapes(g2);

		if (hasHighlights)
			drawHighlight(g2);
	
	}

	private void drawBackground(Graphics2D g2) {
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
	}

	private void drawShapes(Graphics2D g2) {
		for (DrawingShape shape : shapes) {
			shape.draw(g2);
		}
	}

	private MouseAdapter mouseAdapter = new MouseAdapter() {

		@Override
		public void mousePressed(MouseEvent e) {
			int d_X = e.getX();
			int d_Y = e.getY();

			AffineTransform transform = new AffineTransform();
			transform.translate(getWidth() / 2.0, getHeight() / 2.0);
			transform.scale(scale, scale);
			transform.translate( -image.getWidth(null) / 2.0 + w_translateX,
			 -image.getHeight(null) / 2.0 + w_translateY );

			Point2D d_Pt = new Point2D.Double(d_X, d_Y);
			Point2D w_Pt = new Point2D.Double();
			try {
				transform.inverseTransform(d_Pt, w_Pt);
			} catch (NoninvertibleTransformException ex) {
				return;
			}
			int w_X = (int) w_Pt.getX();
			int w_Y = (int) w_Pt.getY();

			boolean hitShape = false;

			Graphics2D g2 = (Graphics2D) getGraphics();
			for (DrawingShape shape : shapes) {
				if (shape.contains(g2, w_X, w_Y)) {
					hitShape = true;
					break;
				}
			}

			if (hitShape) {
				dragging = true;
				w_dragStartX = w_X;
				w_dragStartY = w_Y;
				w_dragStartTranslateX = w_translateX;
				w_dragStartTranslateY = w_translateY;
				dragTransform = transform;
                selectCell(w_X,w_Y);

			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (dragging) {
				int d_X = e.getX();
				int d_Y = e.getY();

				Point2D d_Pt = new Point2D.Double(d_X, d_Y);
				Point2D w_Pt = new Point2D.Double();
				try {
					dragTransform.inverseTransform(d_Pt, w_Pt);
				} catch (NoninvertibleTransformException ex) {
					return;
				}
				int w_X = (int) w_Pt.getX();
				int w_Y = (int) w_Pt.getY();

				int w_deltaX = w_X - w_dragStartX;
				int w_deltaY = w_Y - w_dragStartY;

				w_translateX = w_dragStartTranslateX + w_deltaX;
				w_translateY = w_dragStartTranslateY + w_deltaY;

				notifyTranslationChanged(w_translateX, w_translateY);

				repaint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			initDrag();
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			return;
		}
	};

	private ComponentAdapter componentAdapter = new ComponentAdapter() {

		@Override
		public void componentHidden(ComponentEvent e) {
			return;
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			return;
		}

		@Override
		public void componentResized(ComponentEvent e) {
		}

		@Override
		public void componentShown(ComponentEvent e) {
			return;
		}
	};

	class DrawingImage implements DrawingShape {

		private Image image;
		private Rectangle2D rect;

		public DrawingImage(Image image, Rectangle2D rect) {
			this.image = image;
			this.rect = rect;
		}

		@Override
		public boolean contains(Graphics2D g2, double x, double y) {
			return rect.contains(x, y);
		}

		@Override
		public void draw(Graphics2D g2) {
			g2.drawImage(image, (int) rect.getMinX(), (int) rect.getMinY(),
					(int) rect.getMaxX(), (int) rect.getMaxY(), 0, 0,
					image.getWidth(null), image.getHeight(null), null);
		}

		@Override
		public Rectangle2D getBounds(Graphics2D g2) {
			return rect.getBounds2D();
		}
	}

	// ///////////////
	// Drawing Shape
	// ///////////////

	interface DrawingShape {
		boolean contains(Graphics2D g2, double x, double y);

		void draw(Graphics2D g2);

		Rectangle2D getBounds(Graphics2D g2);
	}

	public void selectCell(int x, int y) {
		if (imageData == null || image == null)
			return;
		Project project = imageData.getProject();
		List<Field> fields = imageData.getFields();

		if (x < imageX || y < imageY || x > imageX + image.getWidth(null)
				|| y > imageY + image.getHeight(null))
			return;

		int currentY = imageY + project.getFirstYCoord();
		for (int i = 0; i < project.getRecordsPerImage(); ++i) {
			int top = currentY;
			int bottom = currentY + (project.getRecordHeight());
			if (y > top && y < bottom) {
				selectedRow = i;
				break;
			}

			currentY += (project.getRecordHeight());
		}

		int currentX = imageX + fields.get(0).getxCoord();
		for (int i = 0; i < fields.size(); ++i) {
			int left = currentX;
			int right = currentX + fields.get(i).getWidth();

			if (x > left && x < right) {
				selectedCol = i;
				break;
			}

			currentX += fields.get(i).getWidth();
		}

		Client.getIndexFrame().getSynchronizer()
				.updateFromImage(selectedRow, selectedCol);
		repaint();
	}

	public void loadBatch(DownloadBatchOutput batchData) {
		imageData = batchData;
		Batch tmpBatch = batchData.getBatch();
		parent.initSpellCheck(imageData.getFields());
		try {
			URL url = new URL(ClientCommunicator.getURL() + File.separator
					+ tmpBatch.getFilePath());

			BufferedImage i = ImageIO.read(url);
			image = deepCopy(i);
			imageBuffered = deepCopy(i);
		} catch (IOException e) {
			System.out.println("Failed to load: " + tmpBatch.getFilePath());
			e.printStackTrace();
		}

		image = image.getScaledInstance((int) (image.getWidth(null) * scale),
				(int) (image.getHeight(null) * scale), Image.SCALE_DEFAULT);

		hasAssignedBatch = true;
		imageX = 350;
		imageY = 50;
		shapes.add(new DrawingImage(image, new Rectangle2D.Double(imageX,
				imageY, image.getWidth(null), image.getHeight(null))));
	}

	// Found online - NOT MINE
	public static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	// -----------------------

	public boolean hasAssignedBatch() {
		return hasAssignedBatch;
	}

	public void toggleHighlights() {
		hasHighlights = !hasHighlights;
	}

	public void zoomIn() {
		if (image == null || imageData == null)
			return;

		
		scale += .05;
		if(scale > MAX_ZOOM)
			scale = MAX_ZOOM;

	}

	public void zoomOut() {
		if (image == null || imageData == null)
			return;

		scale -= .05;
		
		if(scale < MIN_ZOOM)
			scale = MIN_ZOOM;
	}

	public void invert() {
		if (image == null)
			return;

		RescaleOp op = new RescaleOp(-1.0f, 255f, null);

		image = op.filter(imageBuffered, null);
		imageBuffered = op.filter(imageBuffered, null);
		shapes.add(new DrawingImage(image, new Rectangle2D.Double(imageX,
				imageY, image.getWidth(null), image.getHeight(null))));
		isInverted = !isInverted;
		repaint();
	}

	public void setHighlights(boolean highlights) {
		hasHighlights = highlights;
	}

	public void setInverted(boolean inverted) {
		if (inverted)
			invert();
	}

	public void setScale(double zoom) {
		scale = zoom;
	}

	private void drawHighlight(Graphics g) {

		if (image == null || imageData == null || !hasHighlights)
			return;

		g.setColor(new Color(0, 0, 255, 80));

		Project project = imageData.getProject();
		List<Field> fields = imageData.getFields();

		int x = fields.get(selectedCol).getxCoord() + imageX;
		int y = project.getFirstYCoord()
				+ (selectedRow * project.getRecordHeight()) + imageY;
		int w = fields.get(selectedCol).getWidth();
		int h = project.getRecordHeight();

		g.fillRect(x, y, w, h);
	}
}
