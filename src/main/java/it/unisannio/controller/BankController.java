package it.unisannio.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import it.unisannio.model.Account; // Here instances of this class are DTOs
import it.unisannio.service.BranchLocal;

/**
 * Servlet implementation class BankController
 */
@WebServlet("/BankController")
public class BankController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB private BranchLocal branch;
	
	@Resource UserTransaction utx; // To handle user transactions from a Web component
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BankController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = request.getParameter("operation");
		int accountNum; double amount; int secondAccountNum;
		String firstName, lastName, cf; 
		String message = "";
		switch (action) {
			case "deposit":
				try {
					accountNum = Integer.parseInt(request.getParameter("account"));
					amount = Double.parseDouble(request.getParameter("amount"));
				
					branch.deposit(accountNum, amount);
					message = "L'importo " + amount + " è stato accredidato";
				} catch (Exception e) {
					message = "L'importo non può essere accreditato";
				}
				break;
			case "withdraw":
				try {
					accountNum = Integer.parseInt(request.getParameter("account"));
					amount = Double.parseDouble(request.getParameter("amount"));
				
					branch.withdraw(accountNum, amount);
					message = "L'importo " + amount + " è stato addebitato";
				} catch (Exception e) {
					message = "L'importo non può essere addebitato";
				}
				break;
			case "createAccount":
				try {
					cf = request.getParameter("cf"); 
					amount = Double.parseDouble(request.getParameter("amount"));
				
					int num = branch.createAccount(cf, amount);
					message = "Il conto è stato creato ed ha il seguente numero " + num;
				} catch (Exception e) {	
					message = "Non è possibile creare il conto" + e;
				}	
				break;
				
			case "createCust":
				try {
					cf = request.getParameter("cf");
					firstName = request.getParameter("firstName");
					lastName = request.getParameter("lastName");
				
					branch.createCustomer(cf, firstName, lastName);
					message = "l'utente con codice fiscale " + cf + " è stato registrato nel sistema";
				} catch (Exception e) {
					message = "L'utente non può essere registrato";
				}
				break;
			case "allCustomerAccounts":
				try {
					
					cf = request.getParameter("cf");
					List<Account> accounts = branch.getAccounts(cf);
					System.out.println(accounts);
					if (accounts != null)
						for (Account item: accounts) {
							Account a = item;
							message += "Conto N. " + a.getNumber() + " saldo " + a.getBalance() + "\n"; 
						}
					else message ="Nessun conto disponibile";
					
				} catch (Exception e) {
						message = "Codice fiscale errato "+ e;
				}
				break;
			case "transfer":
				try {
					accountNum = Integer.parseInt(request.getParameter("account1"));
					secondAccountNum = Integer.parseInt(request.getParameter("account2"));
					amount = Double.parseDouble(request.getParameter("amount"));
				
				    utx.begin();
					branch.withdraw(accountNum, amount);
					branch.deposit(secondAccountNum, amount);
				    utx.commit();
				    message = "Trasferimento di " + amount + " euro dal conto " + accountNum + " al conto " + secondAccountNum +" avvenuto con successo";
				} catch (Exception e) {
					try { utx.rollback(); message = "Trasferimento non possibile";
					} catch (Exception ee) {}
					
				}
				break;
			default:
				message = "Operazione non supportata";
				break;
		}	
		
		response.getWriter().print(message);
	}

}
