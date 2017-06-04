/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.aut.ense701.gameModel;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 *
 * @author Zhipei Jian
 */
public class RegisterTest {
    Game game = new Game();
    String username = null;
    String password = null;
    String expect = null;
    @Given("^input register username: \"([^\"]*)\"$")
       public void input_register_username(String username) throws Throwable {        
        this.username = username;
        if(username=="123"){
             System.out.println("username exist");
        }
      }
    @Given("^input register password: \"([^\"]*)\"$")
      public void input_register_password(String password) throws Throwable {
        if(password!=""){
            this.password=password;
            System.out.println("password inputed");
        }        
    }
      @When("^Click at Register button$")
      public void Register() throws Throwable {
        System.out.println("Register Button CLicked");   
      }
      
    @Then("^Register Success$")
      public void Register_Successed(){    
        Game game = new Game();    
        expect= game.register(username, password);
        System.out.println(expect);          
      }
    @Then("^Register Fail")
      public void Register_Failed() throws Throwable{     
        Game game = new Game();    
        expect= game.register(username, password);
        System.out.println("Register Fail"); 
      }
}
