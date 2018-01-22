package kubsu.fctam.validator;

import kubsu.fctam.entity.User;
import kubsu.fctam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        if (userService.getByLogin(user.getLogin()) != null) {
            errors.rejectValue("login", "Duplicate.user.login");
        }
    }
}