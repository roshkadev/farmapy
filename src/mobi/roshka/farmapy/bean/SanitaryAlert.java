package mobi.roshka.farmapy.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SanitaryAlert {
/*
farmapy=> \d sanitary_alerts
                                                Table "public.sanitary_alerts"
      Column       |            Type             |                                  Modifiers                                  
-------------------+-----------------------------+-----------------------------------------------------------------------------
 sanitary_alert_id | integer                     | not null default nextval('sanitary_alerts_sanitary_alert_id_seq'::regclass)
 alert_type        | character varying(20)       | 
 description       | character varying(100)      | 
 additional_info   | character varying(1024)     | 
 url               | character varying(200)      | 
 active            | boolean                     | 
 alert_date        | timestamp without time zone | default now()
Indexes:
    "sanitary_alerts_pkey" PRIMARY KEY, btree (sanitary_alert_id)
 */
	
	private static DateFormat _df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	private int sanitaryAlertId;
	private String alertType;
	private String description;
	private String additionalInfo;
	private String url;
	private boolean active;
	private Date alertDate;
	private String alertDateFmt;
	private int ord;
	
	public SanitaryAlert()
	{
		
	}
	
	public static SanitaryAlert fromRS(ResultSet rs) throws SQLException
	{
		SanitaryAlert ret = new SanitaryAlert();
		
		ret.setSanitaryAlertId(rs.getInt("sanitary_alert_id"));
		ret.setAlertType(rs.getString("alert_type"));
		ret.setDescription(rs.getString("description"));
		ret.setAdditionalInfo(rs.getString("additional_info"));
		ret.setUrl(rs.getString("url"));
		ret.setOrd(rs.getInt("ord"));
		ret.setActive(rs.getBoolean("active"));
		ret.setAlertDate(rs.getDate("alert_date"));
		ret.setAlertDateFmt(_df.format(ret.getAlertDate()));
		
		return ret;
	}
	
	public static List<SanitaryAlert> getAll(Connection conn, boolean activeOnly)
	{
		List<SanitaryAlert> ret = new ArrayList<SanitaryAlert>();
		
		try {
			PreparedStatement ps = conn.prepareStatement("select sanitary_alert_id, alert_type, description, additional_info, url, ord, active, alert_date from sanitary_alerts order by ord");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				SanitaryAlert en = SanitaryAlert.fromRS(rs);
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
	
	public int getSanitaryAlertId() {
		return sanitaryAlertId;
	}
	public void setSanitaryAlertId(int sanitaryAlertId) {
		this.sanitaryAlertId = sanitaryAlertId;
	}
	public String getAlertType() {
		return alertType;
	}
	public void setAlertType(String alertType) {
		this.alertType = alertType;
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
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public Date getAlertDate() {
		return alertDate;
	}
	public void setAlertDate(Date alertDate) {
		this.alertDate = alertDate;
	}
	public int getOrd() {
		return ord;
	}
	public void setOrd(int ord) {
		this.ord = ord;
	}

	public String getAlertDateFmt() {
		return alertDateFmt;
	}

	public void setAlertDateFmt(String alertDateFmt) {
		this.alertDateFmt = alertDateFmt;
	}
	
	
	
	
}
