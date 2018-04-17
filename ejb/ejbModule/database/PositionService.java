package database;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class PositionService {

	@PersistenceContext
	private EntityManager em;

	public List<Position> list() {
		return em.createQuery("FROM Position", Position.class).getResultList();
	}

	public Position find(Long id) {
		return em.find(Position.class, id);
	}

	public Long save(Position position) {
		em.persist(position);
		return position.getId();
	}

	public void update(Position position) {
		em.merge(position);
	}

	public void delete(Position position) {
		em.remove(em.contains(position) ? position : em.merge(position));
	}

	public Ziel macToZiel(String s) {
		Query q = em.createQuery("select s from Spiegel as s where s.mac=:MAC");
		q.setParameter("MAC", s);
		List<Spiegel> list = q.getResultList();
		if (list.isEmpty()) {
			return null;
		}
		Spiegel sp = list.get(0);

		// ein oder das Ziel dieses Spiegels
		q = em.createQuery("select z from Ziel as z where z.spiegel=:SPIEGEL");
		q.setParameter("SPIEGEL", sp);
		List<Ziel> listz = q.getResultList();
		if (listz.isEmpty()) {
			return null;
		}
		Ziel ziel = listz.get(0);
		return ziel;
	}

	public int deleteAllPositions() {
		return em.createQuery("delete from Position").executeUpdate();

	}

}
