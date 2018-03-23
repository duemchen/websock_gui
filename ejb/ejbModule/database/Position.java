/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 *
 * @author duemchen
 */
@Entity
public class Position implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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

	public long getX() {
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
		return "db.Pos.[ id=" + id + " ]";
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
}
