package shared.communication;

import shared.model.User;

public class ValidateUserOutput
{
	private boolean valid;
	private User user;

	/**
	 * @param valid <code>true</code> if username/password combination is valid, <code>false</code> otherwise.
	 * @param user The User associated with the username/password combination
	 */
	public ValidateUserOutput(boolean valid, User user)
	{
		this.valid = valid;
		this.user = user;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid()
	{
		return valid;
	}

	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid)
	{
		this.valid = valid;
	}

	/**
	 * @return the user
	 */
	public User getUser()
	{
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user)
	{
		this.user = user;
	}
	
	/**
	 * @return String representation of output
	 */
	public String toString()
	{
		StringBuilder ss = new StringBuilder();
		
		if(valid)
		{
			ss.append("TRUE\n");
			ss.append(user.getFirstName()+ "\n");
			ss.append(user.getLastName() + "\n");
			ss.append(user.getCompletedBatches() + "\n");
		}
		else
		{
			ss.append("FALSE\n");
		}
		
		return ss.toString();
	}
	
}
