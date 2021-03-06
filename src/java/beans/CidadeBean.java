/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import modelo.Cidade;
import dao.CidadeJpaController;
import dao.exceptions.NonexistentEntityException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import modelo.Estado;
import util.JPAUtil;

/**
 *
 * @author denis
 */
@ManagedBean
@RequestScoped
public class CidadeBean {

    /**
     * Creates a new instance of CidadeBean
     */
    private Cidade cidade = new Cidade();
    private Estado estado = new Estado();
    CidadeJpaController daoCidade = new CidadeJpaController(JPAUtil.factory);
    
    private String mensagem;

    public CidadeBean() {
    }

    public void inserir() {
        FacesContext context = FacesContext.getCurrentInstance();
        try{
        cidade.setEstado(estado);
        daoCidade.create(cidade);
        estado = new Estado();
        cidade = new Cidade();
        } catch (Exception ex) {
            context.addMessage("formCidade", new FacesMessage("Cidade não pode ser inserido!"));
            Logger.getLogger(ViagemClienteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        context.addMessage("formCidade", new FacesMessage("Cidade foi inserido com sucesso!"));
    }

    public void excluir() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            daoCidade.destroy(cidade.getId());
            estado = new Estado();
            cidade = new Cidade();
        } catch (Exception ex) {
            context.addMessage("formCidade", new FacesMessage("Cidade não pode ser excluido!"));
            Logger.getLogger(CidadeBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        context.addMessage("formCidade", new FacesMessage("Cidade foi excluido com sucesso!"));
    }

    public void alterar() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            cidade.setEstado(estado);
            daoCidade.edit(cidade);
            estado = new Estado();
            cidade = new Cidade();
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(CidadeBean.class.getName()).log(Level.SEVERE, null, ex);
            context.addMessage("formCidade", new FacesMessage("Cidade não foi alterado!"));
        } catch (Exception ex) {
            Logger.getLogger(CidadeBean.class.getName()).log(Level.SEVERE, null, ex);
            context.addMessage("formCidade", new FacesMessage("Cidade não foi alterado!"));
        }
        context.addMessage("formCidade", new FacesMessage("Cidade foi alterado com sucesso!"));
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public List<Cidade> getCidades() {
        return daoCidade.findCidadeEntities();
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
