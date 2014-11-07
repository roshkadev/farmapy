package mobi.roshka.farmapy.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String code;
	private String icon;
	private List<Category> subcategories;
	
	public Category()
	{
		subcategories = new ArrayList<Category>();
	}
	
	public void addSubCategory(Category c)
	{
		subcategories.add(c);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}


	public List<Category> getSubCategories() {
		return subcategories;
	}


	public void setSubCategories(List<Category> categories) {
		this.subcategories = categories;
	}


	public String getIcon() {
		return icon;
	}


	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	
}
