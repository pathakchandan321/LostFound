package com.lostFound.lostFound.userForm;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserForm {
    @NotBlank(message = "user name required")
    private String name;

    @Email(message = "not a valid email")
    private String email;

    @Size(min=10,max = 10)
    private String phone;
    
    @NotBlank(message = "pasword required")
    @Size(min = 8,message = "8 character is required")
    @Pattern (regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
    message = "required 1 Capital letter 1 small letter 0-9 digits any symbolls")
    private String password;


}
