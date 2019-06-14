package com.vvvTeam.yuglightservice.controller;

import com.vvvTeam.yuglightservice.config.JwtTokenProvider;
import com.vvvTeam.yuglightservice.service.UserService;
import com.vvvTeam.yuglightservice.service.interfaces.LampService;
import com.vvvTeam.yuglightservice.service.request.and.response.Request.LampPropsMsg;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.AuthenticationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;
@RequiredArgsConstructor
@RestController
public class ExternalAPI {
    private final LampService objectsService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/external/add", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")


    public ResponseEntity<?> addListObject(@Valid @RequestBody LampPropsMsg message,
                                           BindingResult bindingResult) throws ParseException {
        Map<String, String> errorsMap = ControllerUtil.getErrorsMap(bindingResult);

        return ResponseEntity.badRequest().build();
    }


    @GetMapping("/external/auth")
    public ResponseEntity signin(@RequestParam String name,@RequestParam String password) {
        AuthenticationRequest data = new AuthenticationRequest(name,password);
        try {
            String username = data.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            String token = jwtTokenProvider.createToken(username, userService.getUserByUsername(username).getRoles());
            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            return ok(model);
        } catch (AuthenticationException e) {
            ResponseEntity.badRequest().build();
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }
    @GetMapping("/external/auth/me")
    public ResponseEntity currentUser(@AuthenticationPrincipal UserDetails userDetails){
        Map<Object, Object> model = new HashMap<>();
        model.put("username", userDetails.getUsername());
        model.put("roles", userDetails.getAuthorities()
                .stream()
                .map(a -> ((GrantedAuthority) a).getAuthority())
                .collect(toList())
        );
        return ok(model);
    }
}
