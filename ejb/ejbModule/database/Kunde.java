/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author duemchen
 */
@Entity
public class Kunde implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "NAME", length = 50, nullable = false)
	private String name;
	@Column(name = "ORT", length = 50)
	private String ort;
	@Column(name = "KENNUNG", length = 50, nullable = false)
	private String kennung; // für den Link zur Application
	@OneToMany(mappedBy = "kunde")
	private List<Spiegel> spiegel;

	public List<Spiegel> getSpiegel() {
		return spiegel;
	}

	public void setSpiegel(List<Spiegel> spiegel) {
		this.spiegel = spiegel;
	}

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getKennung() {
		return kennung;
	}

	public void setKennung(String kennung) {
		this.kennung = kennung;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	private double latitude;// Breite z.B. Äquator=0
	private double longitude;// Länge Observatoriums von Greenwich in London
								// z.B. Berlin 13grd

	public String getName() {
		return name;
	}

	public void setName(String Name) {
		this.name = Name;
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
		if (!(object instanceof Kunde)) {
			return false;
		}
		Kunde other = (Kunde) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "db.Kunde[ id=" + id + ", name:" + name + " ]";
	}

}
