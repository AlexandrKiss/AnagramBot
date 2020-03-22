package ua.kiev.prog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyController {
    private static final Logger logger = LoggerFactory.getLogger(MyController.class);

    private final UserService userService;

    public MyController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String greeting(Model model){
        User user = userService.findByChatId(593845016);
        logger.info(user.toString());
        model.addAttribute("message",user.toString());
        return "course";
    }
}
