package it.unisannio.controller;


import java.net.URI;


import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import it.unisannio.model.Account;
import it.unisannio.model.Customer;
import it.unisannio.service.BranchLocal;


@Consumes("text/plain")
@Produces("text/plain")
@Path("/bank")
public class BankController  {

	@EJB
	private BranchLocal branch;

	@Resource UserTransaction utx; // To handle user transactions from a Web component


	public BankController() {
		super();

	}

	@GET
	public Response test() {
		System.out.println("test");
		try {
			branch.getAccount(1);
			return Response.ok().build();
		} catch (Exception e) {return Response.status(500).build();}

	}

	@POST
	@Path("/accounts/{accountId}/deposits")
	public Response deposit(@PathParam("accountId") int accountNum, double amount) {
		try {

			branch.deposit(accountNum, amount);

			return Response.ok().build();
		} catch (Exception e) {
			System.out.println(e);
			return Response.status(500).build();
		}
	}


	@POST
	@Path("/accounts/{accountId}/withdraws")
	public Response withdraw(@PathParam("accountId") int accountNum, double amount) {
		try {
			branch.withdraw(accountNum, amount);

			return Response.ok().build();
		} catch (Exception e) {
			System.out.println(e);
			return Response.status(500).build();
		}
	}

	@GET
	@Path("/accounts/{accountId}/")
	public Response getBalance(@PathParam("accountId") int accountNum) {
		Account a = branch.getAccount(accountNum);

		try {
			return Response.ok(a.getBalance()).lastModified(a.getLastModified()).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
	}

	@PUT
	@Path("accounts/{accountId}/")
	public Response setBalance(@PathParam("accountId") int accountNum, double amount, @Context Request request) {
		Account a = branch.getAccount(accountNum);
		ResponseBuilder builder = null;
		try {
			builder = request.evaluatePreconditions(a.getLastModified());
			if (builder != null) {
				branch.getAccount(accountNum).setBalance(amount);
			}
			return builder.status(204).build();

		} catch (Exception e) {
			return builder.status(500).build();
		}
	}

	@POST
	@Path("customers/{custCF}/accounts")
	public Response createAccountOfCustomer(@PathParam("custCF") String custCF, double amount) {
		try {

			return Response.created(new URI("/customers/"+custCF+"/accounts/"+branch.createAccount(custCF, amount))).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
	}

	@POST
	@Path("customers")
	@Consumes("application/json")
	public Response createCustomer(Customer c) {
		System.out.println(c);
		try {
			branch.createCustomer(c.getCF(), c.getFirstName(), c.getLastName());
			return Response.created(new URI("/customers/"+c.getCF())).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
	}

	@POST
	@Path("accounts")
	public Response createAccount(@QueryParam("cf") String custCF, double amount) {
		try {
			return Response.created(new URI("/accounts/"+branch.createAccount(custCF, amount))).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
	}

	@POST
	@Path("accounts/transfers")
	public Response transfer(@QueryParam("source") int srcAccount, @QueryParam("destination") int dstAccount, double amount) {
		try {

			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
	}
}
