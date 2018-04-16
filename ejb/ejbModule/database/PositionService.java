package database;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

}
