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
    @Given("^input register username:(\\w+)$")
       public void Register_input_Username(String username) throws Throwable {        
        if(username!=""){
            this.username = username;
            System.out.println("username inputed");
        }
        if(username=="123"){
             System.out.println("username exist");
        }
      }
    @Given("^input register password:(\\s+)$")
      public void Register_input_Password(String password) throws Throwable {
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
        throw new Exception("Register Fail"); 
      }
}
