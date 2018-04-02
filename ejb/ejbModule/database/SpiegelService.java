package database;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class SpiegelService {

	@PersistenceContext
	private EntityManager em;

	public List<Spiegel> list() {
		return em.createQuery("FROM Spiegel", Spiegel.class).getResultList();
	}

	public Spiegel find(Integer id) {
		return em.find(Spiegel.class, id);
	}

	public Long save(Spiegel spiegel) {
		em.persist(spiegel);
		return spiegel.getId();
	}

	public void update(Spiegel spiegel) {
		em.merge(spiegel);
	}

	public void delete(Spiegel spiegel) {
		em.remove(em.contains(spiegel) ? spiegel : em.merge(spiegel));
	}

}
