/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import dao.EstadoJpaController;
import dao.exceptions.NonexistentEntityException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import modelo.Estado;
import util.JPAUtil;

/**
 *
 * @author denis
 */
@ManagedBean
@RequestScoped
public class EstadoBean {

    private Estado estado = new Estado();
    EstadoJpaController daoEstado = new EstadoJpaController(JPAUtil.factory);
    private String mensagem;

    public EstadoBean() {
    }

    public void inserir() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            daoEstado.create(estado);
            estado = new Estado();
        } catch (Exception ex) {
            context.addMessage("formEstado", new FacesMessage("Estado não pode ser inserido"));
            Logger.getLogger(ViagemClienteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        context.addMessage("formEstado", new FacesMessage("Estado foi inserido com sucesso!"));
    }

    public List<modelo.RelatorioEstado> pesquisarInfoDosEstados() {
        return daoEstado.pesquisarInfoDosEstados();
    }

    public void alterar() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            daoEstado.edit(estado);
            estado = new Estado();
        } catch (NonexistentEntityException ex) {
            context.addMessage("formEstado", new FacesMessage("Estado não pode ser alterado"));
            Logger.getLogger(EstadoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            context.addMessage("formEstado", new FacesMessage("Estado não pode ser alterado"));
            Logger.getLogger(EstadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        context.addMessage("formEstado", new FacesMessage("Estado foi inserido com sucesso!"));
    }

    public void excluir() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            daoEstado.destroy(estado.getId());
            estado = new Estado();
        } catch (Exception ex) {
            context.addMessage("formEstado", new FacesMessage("Estado não pode ser excluido"));
            Logger.getLogger(EstadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        context.addMessage("formEstado", new FacesMessage("Estado foi inserido com sucesso"));
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public List<Estado> getEstados() {
        return daoEstado.findEstadoEntities();
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
