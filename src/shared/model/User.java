package shared.model;

import java.io.Serializable;

public class User implements Serializable
{
	private int id;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private int completedBatches;
	private int currentBatchID;
	
	/**
	 * Creates a new instance of <code>User</code> with an unassigned current batch and with 0 completed batches
	 * 
	 * @param id The User's unique ID
	 * @param username The User's username
	 * @param password The User's password
	 * @param firstName The User's first name
	 * @param lastName	The User's last name
	 * @param email	The User's email address
	 * 
	 */
	public User(int id, String username, String password, String firstName,
			String lastName, String email)
	{
		this.id = id;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		
		this.completedBatches = 0;
		this.currentBatchID = -1;
	}

	/**
	 * Creates a new instance of <code>User</code> with an unassigned current batch and with 0 completed batches
	 * 
	 * @param id The User's unique ID
	 * @param username The User's username
	 * @param password The User's password
	 * @param firstName The User's first name
	 * @param lastName	The User's last name
	 * @param email	The User's email address
	 * @param completedBatches The number of batches completed by this user
	 * 
	 */
	public User(int id, String username, String password, String firstName,
			String lastName, String email, int completedBatches)
	{
		this.id = id;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		
		this.completedBatches = completedBatches;
		this.currentBatchID = -1;
	}
	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * @return the username
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}

	/**
	 * @return the completedBatches
	 */
	public int getCompletedBatches()
	{
		return completedBatches;
	}

	/**
	 * @param completedBatches the completedBatches to set
	 */
	public void setCompletedBatches(int completedBatches)
	{
		this.completedBatches = completedBatches;
	}

	/**
	 * @return the currentBatchID
	 */
	public int getCurrentBatchID()
	{
		return currentBatchID;
	}

	/**
	 * @param currentBatchID the currentBatchID to set
	 */
	public void setCurrentBatchID(int currentBatchID)
	{
		this.currentBatchID = currentBatchID;
	}
	
	
}
