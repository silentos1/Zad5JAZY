package rest.services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;

import domain.Person;

@Path("/people")
@Stateless
public class PersonResource {
	@PersistenceContext
	EntityManager em;
	
	@Context
	private HttpServletRequest httpRequest;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(Person p) {
		em.persist(p);
		return Response.ok(p.getId()).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Person> get() {
		TypedQuery<Person> query = em.createNamedQuery("person.all", Person.class);
		
		String page = httpRequest.getParameter("page");
		
		if (page == null || page.equals("")) 
			return query.getResultList();
		int p = Integer.valueOf(page);
		
		query.setMaxResults(3);
		query.setFirstResult((p-1) * 3);
		return query.getResultList();
	}
	
	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") int id) {
		Person result = em.createNamedQuery("person.id", Person.class).setParameter("personId", id).getSingleResult();
		
		if (result == null)
			return Response.status(404).build();
		em.remove(result);
		return Response.ok().build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") int id, Person p) {
		Person result = em.createNamedQuery("person.id", Person.class).setParameter("personId", id).getSingleResult();
		
		if (result == null)
			return Response.status(404).build();
		
		result.setAge(p.getAge());
		result.setBirthday(p.getBirthday());
		result.setEmail(p.getEmail());
		result.setFirstName(p.getFirstName());
		result.setGender(p.getGender());
		result.setLastName(p.getLastName());
		
		return Response.ok().build();
	}
}
