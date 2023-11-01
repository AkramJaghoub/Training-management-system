//package ju.example.training_management_system.model;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.Table;
//import lombok.NoArgsConstructor;
//import jakarta.persistence.DiscriminatorValue;
//
//@Entity
//@Table(name = "Admin")
//@NoArgsConstructor
//public class Admin extends User {
//    public Admin(String email, String password){
//        super(email, password);
//    }
//
//    @JoinColumn(name = "user_id")
//    private Integer userId;
//}