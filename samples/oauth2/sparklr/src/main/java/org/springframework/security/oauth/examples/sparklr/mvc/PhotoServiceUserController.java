package org.springframework.security.oauth.examples.sparklr.mvc;

import org.springframework.security.oauth.examples.sparklr.PhotoServiceUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

/**
 * @author Michael Lavelle
 *         <p/>
 *         Added to provide an endpoint from which Spring Social can obtain authentication details
 */
@RequestMapping("/me")
@Controller
public class PhotoServiceUserController {

    @ResponseBody
    @RequestMapping("")
    public PhotoServiceUser getPhotoServiceUser(Principal principal) {
        return new PhotoServiceUser(principal.getName(), principal.getName());
    }
}
