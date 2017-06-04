/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.aut.ense701.gameModel;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class LoginTest {
    String username = null;
    String password = null;
    String expect = null;
    
     @Given("^input username: \"([^\"]*)\"$")
      public void inputUsername(String username) throws Throwable {     
          this.username=username;
            System.out.println(username);  
         
      }
    @Given("^input password: \"([^\"]*)\"$")
      public void inputPassword(String password) throws Throwable {
          this.password=password;
            System.out.println(password);           
      }

      @When("^Click at Login button$")
      public void login() throws Throwable {
        System.out.println("Button CLicked");   
      }

    @Then("^Login Success$")
      public void loginSuccess(){    
        Game game = new Game();
        expect=game.login(username, password);   
        System.out.println(expect);
        System.out.println("Login Success");
          
      }
        @Then("^Login Fail")
      public void loginFail() throws Throwable{    
        Game game = new Game();
        expect=game.login(username, password);   
        System.out.println(expect);
        System.out.println("Login Fail"); 
      }
}

