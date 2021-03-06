/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import modelo.Cidade;
import java.util.ArrayList;
//import javax.persistence.TypedQuery;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import modelo.Estado;
import modelo.RelatorioEstado;

/**
 *
 * @author denis
 */
public class EstadoJpaController implements Serializable {

    public EstadoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public List<RelatorioEstado> pesquisarInfoDosEstados(){
        EntityManager em = getEntityManager();
        try {
            Query query = em.createQuery("select NEW modelo.RelatorioEstado(e.sigla, count(c.nome), sum(c.populacao)) from Estado e, Cidade c where c.estado.id = e.id group by e.sigla");            
            return query.getResultList();
        } finally {
            em.close();
        }
   }

    public void create(Estado estado) {
        estado.setId(null);        
        if (estado.getCidades() == null) {
            estado.setCidades(new ArrayList<Cidade>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Cidade> attachedCidades = new ArrayList<Cidade>();
            for (Cidade cidadesCidadeToAttach : estado.getCidades()) {
                cidadesCidadeToAttach = em.getReference(cidadesCidadeToAttach.getClass(), cidadesCidadeToAttach.getId());
                attachedCidades.add(cidadesCidadeToAttach);
            }
            estado.setCidades(attachedCidades);
            em.persist(estado);
            for (Cidade cidadesCidade : estado.getCidades()) {
                Estado oldEstadoOfCidadesCidade = cidadesCidade.getEstado();
                cidadesCidade.setEstado(estado);
                cidadesCidade = em.merge(cidadesCidade);
                if (oldEstadoOfCidadesCidade != null) {
                    oldEstadoOfCidadesCidade.getCidades().remove(cidadesCidade);
                    oldEstadoOfCidadesCidade = em.merge(oldEstadoOfCidadesCidade);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Estado estado) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estado persistentEstado = em.find(Estado.class, estado.getId());
//            List<Cidade> cidadesOld = persistentEstado.getCidades();
//            List<Cidade> cidadesNew = estado.getCidades();
//            List<Cidade> attachedCidadesNew = new ArrayList<Cidade>();
//            for (Cidade cidadesNewCidadeToAttach : cidadesNew) {
//                cidadesNewCidadeToAttach = em.getReference(cidadesNewCidadeToAttach.getClass(), cidadesNewCidadeToAttach.getId());
//                attachedCidadesNew.add(cidadesNewCidadeToAttach);
//            }
//            cidadesNew = attachedCidadesNew;
//            estado.setCidades(cidadesNew);
            estado = em.merge(estado);
//            for (Cidade cidadesOldCidade : cidadesOld) {
//                if (!cidadesNew.contains(cidadesOldCidade)) {
//                    cidadesOldCidade.setEstado(null);
//                    cidadesOldCidade = em.merge(cidadesOldCidade);
//                }
//            }
//            for (Cidade cidadesNewCidade : cidadesNew) {
//                if (!cidadesOld.contains(cidadesNewCidade)) {
//                    Estado oldEstadoOfCidadesNewCidade = cidadesNewCidade.getEstado();
//                    cidadesNewCidade.setEstado(estado);
//                    cidadesNewCidade = em.merge(cidadesNewCidade);
//                    if (oldEstadoOfCidadesNewCidade != null && !oldEstadoOfCidadesNewCidade.equals(estado)) {
//                        oldEstadoOfCidadesNewCidade.getCidades().remove(cidadesNewCidade);
//                        oldEstadoOfCidadesNewCidade = em.merge(oldEstadoOfCidadesNewCidade);
//                    }
//                }
//            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = estado.getId();
                if (findEstado(id) == null) {
                    throw new NonexistentEntityException("The estado with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estado estado;
            try {
                estado = em.getReference(Estado.class, id);
                estado.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estado with id " + id + " no longer exists.", enfe);
            }
//            List<Cidade> cidades = estado.getCidades();
//            for (Cidade cidadesCidade : cidades) {
//                cidadesCidade.setEstado(null);
//                cidadesCidade = em.merge(cidadesCidade);
//            }
            em.remove(estado);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Estado> findEstadoEntities() {
        return findEstadoEntities(true, -1, -1);
    }

    public List<Estado> findEstadoEntities(int maxResults, int firstResult) {
        return findEstadoEntities(false, maxResults, firstResult);
    }

    private List<Estado> findEstadoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Estado as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Estado findEstado(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Estado.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstadoCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Estado as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
