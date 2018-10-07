/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author duemchen
 * 
 *         {"topic":"simago/compass/74-DA-38-3E-E8-3C","time":"26.04.2017
 *         09:31:17","cmd":"save","roll":-16,"dir":13,"mirrorid":"2","pitch":-20}
 * 
 */
@Entity
@Table(name = "POSINEU")
public class Position implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "pos_generator")
	@SequenceGenerator(name = "pos_generator", sequenceName = "pos_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private String data;

	@ManyToOne
	@JoinColumn(name = "ZIELID", nullable = false)
	private Ziel ziel;

	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date datum; // zeitpunkt der messung

	// Position 3 dimensional
	private long x;
	private long y;
	private long z;
	private boolean loesch; // deaktivieren

	public final static SimpleDateFormat simpleDatetimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	public final static SimpleDateFormat simpleDatetimeFormatZeit = new SimpleDateFormat("HH:mm");
	public final static SimpleDateFormat simpleDatetimeFormatDatum = new SimpleDateFormat("yy-MM-dd");
	public final static SimpleDateFormat simpleDatetimeFormatZD = new SimpleDateFormat("dd.MM.yy HH:mm");

	// {"topic":"simago/compass/74-DA-38-3E-E8-3C","time":"26.04.2017
	// 09:31:17","cmd":"save","roll":-16,"dir":13,"mirrorid":"2","pitch":-20}
	// {"topic":"simago/compass/74-DA-38-3E-E8-3C","time":"26.04.2017
	// 09:31:17","cmd":"save","roll":-16,"dir":13,"mirrorid":"2","pitch":-20}
	public Position(JSONObject jo) throws JSONException, ParseException {
		super();
		this.data = jo.toString();
		this.datum = simpleDatetimeFormat.parse(jo.getString("time"));
		this.loesch = false;
		this.x = jo.getInt("dir");
		this.y = jo.getInt("pitch");
		this.z = jo.getInt("roll");

	}

	public Position() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Ziel getZiel() {
		return ziel;
	}

	public void setZiel(Ziel ziel) {
		this.ziel = ziel;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	private long getX() {
		return x;
	}

	public void setX(long x) {
		this.x = x;
	}

	public long getY() {
		return y;
	}

	public void setY(long y) {
		this.y = y;
	}

	public long getZ() {
		return z;
	}

	public void setZ(long z) {
		this.z = z;
	}

	public boolean isLoesch() {
		return loesch;
	}

	public void setLoesch(boolean loesch) {
		this.loesch = loesch;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Position)) {
			return false;
		}
		Position other = (Position) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "db.Pos.[ id=" + id + ", xk:" + getX180() + ", y:" + getY() + ", z:" + getZ() + " ]";
	}

	public String toStringAZ() {
		return "azimuth:" + getX180() + ", zenith:" + getProjectionXy(id) + " ]";
	}

	public long getX180() {
		long result = getX();
		// 0 wird zu 180
		result += 180;
		if (result > 360) {
			result -= 360;
		}

		return result * -1 + 360;
	}

	/**
	 * Projection auf xy Ebene
	 * 
	 * @return
	 */
	public double getProjectionXy3() {
		double FAKTOR = 1.6 * Math.PI;
		double result = Math.asin(Math.sin(Math.toRadians(-getY())) * Math.cos(Math.toRadians(getZ() * FAKTOR)));
		result = Math.toDegrees(result);
		result = Math.round(100.0 * result) / 100.0;
		// result = -getY();
		return result;
	}

	public double getProjectionXy6() {
		double FAKTOR = 1;// 2 * Math.PI;
		double result = Math.asin(Math.sin(Math.toRadians(-getY())) * Math.cos(Math.toRadians(getZ() * FAKTOR)));
		result = Math.toDegrees(result);
		result = Math.round(100.0 * result) / 100.0;
		return result;
	}

	public double getProjectionXy(long zielID) {
		if (zielID == 3)
			return getProjectionXy3();
		else
			return getProjectionXy6();
	}

	public String getZP() {
		return simpleDatetimeFormatZD.format(datum);
	}

	/**
	 * 0...24.0
	 * 
	 * @return
	 */
	public double getUhrzeitDez() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(getDatum());
		double minuten = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
		double tagTeil = minuten / (60 * 24);
		System.out.println(tagTeil);

		DecimalFormat f = new DecimalFormat("#0.00");
		return Double.parseDouble(f.format(tagTeil * 24).replaceAll(",", "."));

	}

	public static void main(String[] args) {

		Position pos = new Position();
		pos.setDatum(new Date());
		System.out.println(pos.getUhrzeitDez());

	}

}
