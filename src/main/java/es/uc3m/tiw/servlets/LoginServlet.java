package es.uc3m.tiw.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import es.uc3m.tiw.dominios.Producto;
import es.uc3m.tiw.dominios.Usuario;
//import es.uc3m.tiw.Conector;
import es.uc3m.tiw.daos.UsuarioDAO;
import es.uc3m.tiw.daos.UsuarioDAOImpl;
import es.uc3m.tiw.daos.ProductoDAO;
import es.uc3m.tiw.daos.ProductoDAOImpl;

	/**
	 * 
	 * 
	 * @author Grupo 3 - TIW 2016
	 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final String LOGIN_JSP = "/login.jsp";
	private static final String ERROR_JSP = "/error.jsp";
	private static final String HOME_JSP = "/home.jsp";
	private static final long serialVersionUID = 1L;
	private ServletConfig config;
	private List<Usuario> usuarios;
	private List<Producto> productos;
	private UsuarioDAO dao;
	private ProductoDAO pdao; 
	@PersistenceContext(unitName="WallapopTIW")
	private EntityManager em;
	@Resource
	UserTransaction ut;
		
				@Override
				public void init(ServletConfig config) throws ServletException {
					this.config = config;
					
					pdao = new ProductoDAOImpl(); 
					pdao.setConexion(em);
					pdao.setTransaction(ut);
					
					dao = new UsuarioDAOImpl(); 
					dao.setConexion(em);
					dao.setTransaction(ut);
					
					
			
		}
	       


		/**
		 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
		 */
		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			config.getServletContext().getRequestDispatcher(LOGIN_JSP).forward(request, response);
			
		}

		/**
		 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
		 */
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			String email = request.getParameter("email");
			String password = request.getParameter("clave");
			String mensaje ="";
			String pagina = "";
			pagina = LOGIN_JSP;
			usuarios = new ArrayList<Usuario>();
			productos = new ArrayList<Producto>();
			
			HttpSession sesion = request.getSession();
			
			try {
				productos = (List<Producto>) pdao.listarProductos(); 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			try {
				usuarios = (List<Usuario>) dao.listarUsuarios();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			Usuario u = comprobarUsuario(email, password);
	
			if (u != null){
				
				
				pagina = HOME_JSP;
				sesion.setAttribute("productos", productos);
				//request.setAttribute("usuarios", usuarios);
				sesion.setAttribute("usuario", u);
				sesion.setAttribute("autenticado", true);
				
				
				
			}else{
				
				mensaje = "Usuario o password incorrectos";
				request.setAttribute("mensaje", mensaje);
			}
			
				config.getServletContext().getRequestDispatcher(pagina).forward(request, response);
				
			
		}



		private Usuario comprobarUsuario(String email, String password) {
			Usuario u = null;
			for (Usuario usuario : usuarios) {
				if (email.equals(usuario.getEmail()) && password.equals(usuario.getPassword())){
					u = usuario;
					//break;
				}
			}
			return u;
		}

}