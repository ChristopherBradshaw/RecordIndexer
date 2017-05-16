package shared.communication;

import java.util.List;

import shared.model.Project;

public class GetProjectsOutput
{
	private List<Project> projects;

	/**
	 * @param projects Project results
	 */
	public GetProjectsOutput(List<Project> projects)
	{
		this.projects = projects;
	}

	/**
	 * @return the projects
	 */
	public List<Project> getProjects()
	{
		return projects;
	}

	/**
	 * @param projects the projects to set
	 */
	public void setProjects(List<Project> projects)
	{
		this.projects = projects;
	}
	
	/**
	 * @return String representation of output
	 */
	public String toString()
	{
		StringBuilder ss = new StringBuilder();
		if(projects == null)
		{
			ss.append("FAILED\n");
		}
		else
		{
			for(Project p : projects)
			{
				ss.append(p.getId() + "\n" + p.getTitle() + "\n");
			}
		}
		
		return ss.toString();
	}
	
}
