/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.infra.web.controller;

import com.google.gson.GsonBuilder;
import org.egov.infra.admin.common.entity.Favourites;
import org.egov.infra.admin.common.entity.MenuLink;
import org.egov.infra.admin.common.service.FavouritesService;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.admin.master.service.ModuleService;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.config.properties.ApplicationProperties;
import org.egov.infra.persistence.entity.enums.UserType;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.validation.ValidatorUtils;
import org.egov.infra.web.support.ui.Menu;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/home")
public class HomeController {

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private FavouritesService favouritesService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private CityService cityService;

    @Autowired
    private ValidatorUtils validatorUtils;

    @RequestMapping
    public String showHome(HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap modelData) {
        User user = securityUtils.getCurrentUser();
        setUserLocale(user, request, response);
        if (securityUtils.currentUserType().equals(UserType.CITIZEN))
            return "redirect:/../portal/home";
        else
            return prepareOfficialHomePage(user, session, modelData);
    }

    @ResponseBody
    @RequestMapping(value = "favourite/add", method = POST)
    public boolean addFavourite(@Valid @ModelAttribute Favourites favourites, BindingResult bindResult) {
        return !bindResult.hasErrors() && favouritesService.addToCurrentUserFavourite(favourites).getId() != null;
    }

    @ResponseBody
    @RequestMapping(value = "favourite/remove")
    public boolean removeFavourite(@RequestParam Integer actionId) {
        return favouritesService.removeFromCurrentUserFavourite(actionId);
    }

    @ResponseBody
    @RequestMapping(value = "password/update")
    public String changePassword(@RequestParam String currentPwd, @RequestParam String newPwd, @RequestParam String retypeNewPwd) {
        User user = securityUtils.getCurrentUser();
        if (passwordEncoder.matches(currentPwd, user.getPassword())) {
            if (!validatorUtils.isValidPassword(newPwd))
                return "NEWPWD_INVALID";
            if (newPwd.equals(retypeNewPwd)) {
                user.setPassword(passwordEncoder.encode(newPwd));
                user.setPwdExpiryDate(new DateTime().plusDays(applicationProperties.userPasswordExpiryInDays()).toDate());
                userService.updateUser(user);
                return "SUCCESS";
            }
            return "NEWPWD_UNMATCH";
        }
        return "CURRPWD_UNMATCH";
    }

    @ResponseBody
    @RequestMapping(value = "feedback/sent")
    public boolean sendFeedback(@RequestParam String subject, @RequestParam String message, HttpSession session) {
        cityService.sentFeedBackMail((String) session.getAttribute("corpContactEmail"), subject,
                message + " \n Regards \n " + user().getName());
        return true;
    }

    @ModelAttribute("user")
    public User user() {
        return securityUtils.getCurrentUser();
    }

    @RequestMapping(value = "profile/edit", method = GET)
    public String editProfile() {
        return "profile-edit";
    }

    @RequestMapping(value = "profile/edit", method = POST)
    public String saveProfile(@Valid @ModelAttribute User user, BindingResult binder, HttpServletRequest request,
                              HttpServletResponse response, RedirectAttributes redirAttrib) {
        if (binder.hasErrors())
            return "profile-edit";
        userService.updateUser(user);
        setUserLocale(user, request, response);
        redirAttrib.addFlashAttribute("message", "msg.profile.update.success");
        return "redirect:/home/profile/edit";
    }

    private void setUserLocale(User user, HttpServletRequest request, HttpServletResponse response) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        localeResolver.setLocale(request, response, user.locale());
    }

    private String prepareOfficialHomePage(User user, HttpSession session, ModelMap modelData) {
        modelData.addAttribute("menu", prepareApplicationMenu(moduleService.getMenuLinksForRoles(user.getRoles()), user));
        modelData.addAttribute("userName", user.getName() == null ? "Anonymous" : user.getName());
        modelData.addAttribute("app_version", applicationProperties.appVersion());
        modelData.addAttribute("app_buildno", applicationProperties.appBuildNo());
        if (!applicationProperties.devMode()) {
            modelData.addAttribute("app_core_build_no", applicationProperties.appCoreBuildNo());
            modelData.addAttribute("dflt_pwd_reset_req", checkDefaultPasswordResetRequired(user));
            int daysToExpirePwd = daysToExpirePassword(user);
            modelData.addAttribute("pwd_expire_in_days", daysToExpirePwd);
            modelData.addAttribute("warn_pwd_expire", daysToExpirePwd <= 5);
        }
        modelData.addAttribute("issue_report_url", applicationProperties.issueReportingUrl());
        session.setAttribute("app_release_no", applicationProperties.appVersion() + "_" + applicationProperties.appBuildNo());
        return "home";
    }

    private List<MenuLink> getEmployeeSelfService(List<MenuLink> menuLinks, User user) {
        return menuLinks.parallelStream().filter(menuLink -> menuLink.getName().equals("EmployeeSelfService")).findFirst()
                .map(menuLink -> moduleService.getMenuLinksByParentModuleId(menuLink.getId(), user.getId()))
                .orElse(Collections.emptyList());

    }

    private String prepareApplicationMenu(List<MenuLink> menuLinks, User user) {
        Menu menu = new Menu();
        menu.setId("menuID");
        menu.setTitle(" ");
        menu.setIcon("fa fa-reply-all");
        menu.setItems(new LinkedList<>());
        List<MenuLink> favourites = moduleService.getUserFavouritesMenuLinks(user.getId());
        createApplicationMenu(menuLinks, favourites, user, menu);
        List<MenuLink> essMenus = getEmployeeSelfService(menuLinks, user);
        if (!essMenus.isEmpty())
            createSelfServiceMenu(essMenus, menu);
        createFavouritesMenu(favourites, menu);

        return "[" + new GsonBuilder().create().toJson(menu) + "]";
    }

    private void createApplicationMenu(List<MenuLink> menuLinks, List<MenuLink> favourites, User user, Menu menu) {
        Menu applicationMenu = createSubmenu("apps", "Applications", "Applications", "javascript:void(0);",
                "fa fa-th floatLeft", menu);
        menuLinks.stream().filter(menuLink -> !menuLink.getName().equals("EmployeeSelfService")).forEach(menuLink -> {
            createSubmenuRoot(menuLink, favourites, user, applicationMenu);
        });
    }

    private void createFavouritesMenu(List<MenuLink> favourites, Menu menu) {
        Menu favouritesMenu = createSubmenu("favMenu", "Favourites", "Favourites", "javascript:void(0);",
                "fa fa-star floatLeft", menu);
        favourites.stream().forEach(favourite -> {
            Menu appLinks = new Menu();
            appLinks.setId("fav-" + favourite.getId());
            appLinks.setName(favourite.getName());
            appLinks.setLink("/" + favourite.getUrl());
            appLinks.setIcon("fa fa-times-circle remove-favourite");
            favouritesMenu.getItems().add(appLinks);
        });
    }

    private void createSelfServiceMenu(List<MenuLink> selfServices, Menu menu) {
        Menu selfServiceMenu = createSubmenu("ssMenu", "Self Service", "Self Service", "javascript:void(0);",
                "fa fa-ellipsis-h floatLeft", menu);
        selfServices.stream().forEach(selfService -> {
            Menu appLinks = new Menu();
            appLinks.setName(selfService.getName());
            appLinks.setLink("/" + selfService.getContextRoot() + selfService.getUrl());
            selfServiceMenu.getItems().add(appLinks);

        });
    }

    private void createApplicationLink(MenuLink childMenuLink, List<MenuLink> favourites, User user, Menu parent) {
        if (childMenuLink.isEnabled()) {
            Menu appLink = new Menu();
            appLink.setId(childMenuLink.getId().toString());
            appLink.setIcon(
                    "fa fa-star floatLeft " + (favourites.contains(childMenuLink) ? "added-as-fav" : "add-to-favourites"));
            appLink.setName(childMenuLink.getName());
            appLink.setLink("/" + childMenuLink.getContextRoot() + childMenuLink.getUrl());
            parent.getItems().add(appLink);
        } else {
            createSubmenuRoot(childMenuLink, favourites, user, parent);
        }
    }

    private void createSubmenuRoot(MenuLink childMenuLink, List<MenuLink> favourites, User user, Menu parent) {
        List<MenuLink> submodules = moduleService.getMenuLinksByParentModuleId(childMenuLink.getId(), user.getId());
        if (!submodules.isEmpty()) {
            Menu submenu = createSubmenu(String.valueOf(childMenuLink.getId()), childMenuLink.getName(),
                    childMenuLink.getName(), "javascript:void(0);", "", parent);
            submodules.stream().forEach(submodule -> createApplicationLink(submodule, favourites, user, submenu));
        }
    }

    private Menu createSubmenu(String id, String name, String title, String link, String icon, Menu parent) {
        Menu submenuItem = new Menu();
        submenuItem.setId(id);
        submenuItem.setName(name);
        submenuItem.setIcon(icon);
        submenuItem.setLink(link);
        submenuItem.setItems(new LinkedList<>());
        parent.getItems().add(submenuItem);

        Menu submenu = new Menu();
        submenu.setTitle(title);
        submenu.setIcon("");
        submenu.setItems(new LinkedList<>());
        submenuItem.getItems().add(submenu);

        return submenu;
    }

    private boolean checkDefaultPasswordResetRequired(User user) {
        return passwordEncoder.matches("12345678", user.getPassword()) || passwordEncoder.matches("demo", user.getPassword());
    }

    private int daysToExpirePassword(User user) {
        return Days.daysBetween(new LocalDate(), user.getPwdExpiryDate().toLocalDate()).getDays();
    }

}
