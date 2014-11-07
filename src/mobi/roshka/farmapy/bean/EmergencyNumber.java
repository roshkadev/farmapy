package mobi.roshka.farmapy.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmergencyNumber {
/*
farmapy=> select * from emergency_numbers;
 emergency_no_id | no_01 | no_02 | no_03 | description | additional_info | url | ord | active 
-----------------+-------+-------+-------+-------------+-----------------+-----
 */
	
	private int emergencyId;
	private String no01;
	private String no02;
	private String no03;
	private String description;
	private String additionalInfo;
	private String url;
	private int ord;
	private boolean active;
	private String type;
	
	public EmergencyNumber()
	{
		
	}
	
	public static EmergencyNumber fromRS(ResultSet rs) throws SQLException
	{
		EmergencyNumber ret = new EmergencyNumber();
		
		ret.setEmergencyId(rs.getInt("emergency_no_id"));
		ret.setNo01(rs.getString("no_01"));
		ret.setNo02(rs.getString("no_02"));
		ret.setNo03(rs.getString("no_03"));
		ret.setDescription(rs.getString("description"));
		ret.setAdditionalInfo(rs.getString("additional_info"));
		ret.setUrl(rs.getString("url"));
		ret.setType(rs.getString("emergency_type"));
		ret.setOrd(rs.getInt("ord"));
		ret.setActive(rs.getBoolean("active"));
		return ret;
	}
	
	public static List<EmergencyNumber> getAll(Connection conn, boolean activeOnly)
	{
		List<EmergencyNumber> ret = new ArrayList<EmergencyNumber>();
		
		try {
			PreparedStatement ps = conn.prepareStatement("select emergency_no_id, no_01, no_02, no_03, description, additional_info, url, ord, emergency_type, active from emergency_numbers order by ord");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				EmergencyNumber en = EmergencyNumber.fromRS(rs);
				if (!activeOnly || en.isActive())
					ret.add(en);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static List<EmergencyNumber> getAll(Connection conn)
	{
		return EmergencyNumber.getAll(conn, true);
	}

	public int getEmergencyId() {
		return emergencyId;
	}

	public void setEmergencyId(int emergencyId) {
		this.emergencyId = emergencyId;
	}

	public String getNo01() {
		return no01;
	}

	public void setNo01(String no01) {
		this.no01 = no01;
	}

	public String getNo02() {
		return no02;
	}

	public void setNo02(String no02) {
		this.no02 = no02;
	}

	public String getNo03() {
		return no03;
	}

	public void setNo03(String no03) {
		this.no03 = no03;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getOrd() {
		return ord;
	}

	public void setOrd(int ord) {
		this.ord = ord;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
	
	
}
