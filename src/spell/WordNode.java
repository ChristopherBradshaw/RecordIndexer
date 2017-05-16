package spell;

import java.util.HashMap;

import spell.ITrie.INode;

public class WordNode implements INode
{
	private int count;
	private WordNode parent;
	private HashMap<Character,WordNode> children;
	private boolean isWord;
	
	WordNode()
	{
		this.count = 0;
		this.parent = null;
		this.children = new HashMap<Character,WordNode>();
		this.isWord = false;
	}
	
	public WordNode getChild(char key)
	{
		return children.get(key);
	}
	
	public boolean isWord()
	{
		return isWord;
	}
	public void setWord(boolean word)
	{
		this.isWord = word;
	}
	public void setParent(WordNode parentP)
	{
		this.parent = parentP;
	}

	/**
	 * 
	 * @param c Key to be instantiated
	 * @return Node value created by key 'c'. If key/val pair already existed, return null
	 */
	public WordNode setChild(char c)
	{
		if(children.get(c) != null)
			return null;
		
		 WordNode newNode = children.put(c, new WordNode());
		 children.get(c).setParent(this);
		 return newNode;
	}
	public WordNode getParent()
	{	
		return this.parent;
	}
	
	public void setValue(int countP)
	{
		this.count = countP;
	}
	@Override
	public int getValue()
	{
		return this.count;
	}
	
	public int getDepth()
	{
		if(parent == null)
			return -1;
		
		return parent.getDepth() + 1;
	}
	
	public int getChildrenSize()
	{
		if(this.children == null)
			return -1;
		
		return this.children.size();
	}
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		result = prime * result + count;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WordNode other = (WordNode) obj;
		if (children == null)
		{
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		
		if (count != other.count)
			return false;
	
		return true;
	}

}
