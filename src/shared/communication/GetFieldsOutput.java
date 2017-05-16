package shared.communication;

import java.util.List;

import shared.model.Field;

public class GetFieldsOutput
{
	private List<Field> fields;

	/**
	 * @param fields Field results
	 */
	public GetFieldsOutput(List<Field> fields)
	{
		this.fields = fields;
	}

	/**
	 * @return the fields
	 */
	public List<Field> getFields()
	{
		return fields;
	}
	
	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<Field> fields)
	{
		this.fields = fields;
	}
	
	/**
	 * @return String representation of output
	 */
	public String toString()
	{
		StringBuilder ss = new StringBuilder();
		for(Field f : fields)
		{
			ss.append(f.getForeignProjectId() + "\n");
			ss.append(f.getId() + "\n");
			ss.append(f.getTitle() + "\n");
		}
		
		return ss.toString();
	}
}
