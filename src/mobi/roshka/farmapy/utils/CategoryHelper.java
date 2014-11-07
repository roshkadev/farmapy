package mobi.roshka.farmapy.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import mobi.roshka.farmapy.bean.Category;
import mobi.roshka.farmapy.comm.JSONProcessor;

public class CategoryHelper {

	public  static final String CATEGORIES_ALL = "categories.all";
	private List<Category> categories;
	private Map<String, Category> categoriesMap;
	
	
	private void createCategoriesMap()
	{
		categoriesMap = new HashMap<String, Category>();
	
		addCategoryDFSHelper(categories);
	}
	
	
	private void addCategoryDFSHelper(List<Category> categories) {
		for (Category c : categories) {
			categoriesMap.put(c.getCode(), c);
			List<Category> subCategories  = c.getSubCategories();
			if(subCategories != null) {
				addCategoryDFSHelper(subCategories);
			}
		}
	}
	
	public CategoryHelper(JSONArray jsonCategories) throws JSONException
	{
		categories = JSONProcessor.getCategories(jsonCategories);
		createCategoriesMap();
	}
	
	public Category findByCode(String code)
	{
		return categoriesMap.get(code);
	}
	
	/**
	 * 
	 * @param searchName
	 * @return
	 */
	public List <Category> findByName(String searchName) {
		List <Category> result = new ArrayList<Category>();
		
		findByNameDFSHelper(searchName, this.categories, result);
		
		return result;
	}
	
	/**
	 * 
	 * @param searchName
	 * @param categories list of categories to be scanned by searchName
	 * @param result list to put the results in, so we don't create a new list on each call and have to merge the results later.
	 * @return result
	 */
	private List <Category> findByNameDFSHelper(String searchName, List <Category> categories, List <Category> result) {
		
		for (Category c : categories) {
			if (c.getName().toLowerCase().contains(searchName.toLowerCase())) {
				result.add(c);
				findByNameDFSHelper(searchName, c.getSubCategories(), result);
			}
		}				
		return result;
	}
	
	public List<Category> getCategories()
	{
		return categories;
	}
	
}
