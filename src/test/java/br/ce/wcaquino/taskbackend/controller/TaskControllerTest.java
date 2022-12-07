package br.ce.wcaquino.taskbackend.controller;

import java.time.LocalDate;

import org.junit.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.taskbackend.model.Task;
import br.ce.wcaquino.taskbackend.repo.TaskRepo;
import br.ce.wcaquino.taskbackend.utils.ValidationException;

public class TaskControllerTest {

	@Mock
	private TaskRepo todoRepo;
	
	@InjectMocks
	private TaskController controller;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void naoDeveSalvarTarefaSemDescricao() {
		Task todo = new Task();
		todo.setDueDate(LocalDate.now());
		
		controller = new TaskController();
		try {
			controller.save(todo);
			Assert.fail("Não deveria chegar nesse ponto.");
		} catch (ValidationException e) {
			Assert.assertEquals(e.getMessage(), "Fill the task description");
		}
	}
	
	@Test
	public void naoDeveSalvarTarefaSemData() {
		Task todo = new Task();
		todo.setTask("Teste");
		
		controller = new TaskController();
		try {
			controller.save(todo);
			Assert.fail("Não deveria chegar nesse ponto.");
		} catch (ValidationException e) {
			Assert.assertEquals(e.getMessage(), "Fill the due date");
		}
	}
	
	@Test
	public void naoDeveSalvarTarefaComDataPassada() {
		Task todo = new Task();
		todo.setTask("Teste");
		todo.setDueDate(LocalDate.of(2010, 01, 01));
		
		controller = new TaskController();
		try {
			controller.save(todo);
			Assert.fail("Não deveria chegar nesse ponto.");
		} catch (ValidationException e) {
			Assert.assertEquals(e.getMessage(), "Due date must not be in past");
		}
	}
	
	@Test
	public void deveSalvarTarefaComSucesso() throws ValidationException {
		Task todo = new Task();
		todo.setTask("Descricao");
		todo.setDueDate(LocalDate.now());
		
		controller.save(todo);
		Mockito.verify(todoRepo).save(todo);
	}
}
