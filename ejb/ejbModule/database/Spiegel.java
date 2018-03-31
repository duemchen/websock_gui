/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author duemchen
 */
@Entity
public class Spiegel implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "KUNDENID", nullable = false)
	private Kunde kunde;
	@Column(name = "NAME", length = 50, nullable = false)
	private String name;
	@Column(name = "MAC", length = 20, nullable = false)
	private String mac;
	@Column(name = "CAMERA", length = 100)
	private String camera; // mqtt Link zur Kamera, wenn vorhanden
	// abschaltkriterien
	double sonnenhoehe; // mindesthöhe der Sonne
	double sonnenwinkelMorgens; // aktiv wenn grösser als
	double sonnenwinkelAbends; // aktiv wenn kleiner als
	double windmax; // Abschaltung wenn höher
	double wolkenmax; // Abschaltung wenn höher
	// Ruhestellungen speichern für die Situationen:
	double wind; // deaktiv wegen wind
	double wolke; // deaktiv wegen wolken
	double ruhe; // deaktiv wegen Nacht oder Sonne zu niedrig
	Double ziel; // das eingestellte Ziel muss evtl. woanders hin

	public Kunde getKunde() {
		return kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getCamera() {
		return camera;
	}

	public void setCamera(String camera) {
		this.camera = camera;
	}

	public double getSonnenhoehe() {
		return sonnenhoehe;
	}

	public void setSonnenhoehe(double sonnenhoehe) {
		this.sonnenhoehe = sonnenhoehe;
	}

	public double getSonnenwinkelMorgens() {
		return sonnenwinkelMorgens;
	}

	public void setSonnenwinkelMorgens(double sonnenwinkelMorgens) {
		this.sonnenwinkelMorgens = sonnenwinkelMorgens;
	}

	public double getSonnenwinkelAbends() {
		return sonnenwinkelAbends;
	}

	public void setSonnenwinkelAbends(double sonnenwinkelAbends) {
		this.sonnenwinkelAbends = sonnenwinkelAbends;
	}

	public double getWindmax() {
		return windmax;
	}

	public void setWindmax(double windmax) {
		this.windmax = windmax;
	}

	public double getWolkenmax() {
		return wolkenmax;
	}

	public void setWolkenmax(double wolkenmax) {
		this.wolkenmax = wolkenmax;
	}

	public double getWind() {
		return wind;
	}

	public void setWind(double wind) {
		this.wind = wind;
	}

	public double getWolke() {
		return wolke;
	}

	public void setWolke(double wolke) {
		this.wolke = wolke;
	}

	public double getRuhe() {
		return ruhe;
	}

	public void setRuhe(double ruhe) {
		this.ruhe = ruhe;
	}

	public Double getZiel() {
		return ziel;
	}

	public void setZiel(Double ziel) {
		this.ziel = ziel;
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
		if (!(object instanceof Spiegel)) {
			return false;
		}
		Spiegel other = (Spiegel) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "db.Spiegel[ id=" + id + ", " + name + " ]";
	}

}
