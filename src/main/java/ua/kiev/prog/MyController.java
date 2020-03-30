package ua.kiev.prog;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.kiev.prog.models.CustomUser;

import java.util.List;

@Controller
public class MyController {
//    private static final Logger logger = LoggerFactory.getLogger(MyController.class);

    private final UserService userService;

    public MyController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String greeting(@RequestParam(name="id", required=false) Long id, Model model){
        if(id!=null) {
            CustomUser customUser = userService.findByChatId(id);
            model.addAttribute("customUser",customUser);
            return "user";
        } else {
            List<CustomUser> users = userService.findAllUsers();
            model.addAttribute("users",users);
            return "users";
        }
    }

    @GetMapping("/chat")
    public String viewChat(){
        return "chat";
    }
}
