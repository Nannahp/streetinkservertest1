package com.example.streetinkbookingsystem.controllers;

import com.example.streetinkbookingsystem.models.Booking;
import com.example.streetinkbookingsystem.models.TattooArtist;
import com.example.streetinkbookingsystem.services.BookingService;
import com.example.streetinkbookingsystem.services.LoginService;
import com.example.streetinkbookingsystem.services.TattooArtistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Controller
public class DayController {

    @Autowired
    BookingService bookingService;
    @Autowired
    LoginService loginService;
    @Autowired
    TattooArtistService tattooArtistService;

    /**
     * @author Nanna
     * @param session used to determine if the user is logged in, if not then redirect to index page
     * @param date used to display details about the current day and get bookings associated with the date
     * @return day view with a list of booking for that day
     */
    @GetMapping("/day")
    public String seeDay(Model model, HttpSession session, @RequestParam LocalDate date) {
        boolean loggedIn = loginService.isUserLoggedIn(session);
        if (!loggedIn) {
            return "redirect:/";
        }

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("username", username);
        TattooArtist tattooArtist = tattooArtistService.getTattooArtistByUsername(username);
        model.addAttribute("tattooArtist", tattooArtist);

        List<Booking> bookingList = bookingService.getBookingsForDay(date, username);
        model.addAttribute("bookingList", bookingList);
        model.addAttribute("date", date);

        // might move to service, this makes a list of quarter hours
        List<Double> hours = new ArrayList<>();
        for (double hour = 9; hour <= 20; hour += 0.25) {
            hours.add(hour);
        }
        model.addAttribute("hours", hours);
        model.addAttribute("bookingService", bookingService);

        return "home/day";
    }

    /**
     * @author Nanna
     * @param date used to determine the next day by adding a day
     * @return day view for the next day
     */
    @PostMapping("/day/next")
    public String seeNextDay( @RequestParam LocalDate date) {
        LocalDate nextDate = date.plusDays(1);
        return "redirect:/day?date=" + nextDate;
    }
    /**
     * @author Nanna
     * @param date used to determine the previous day by subtracting a day
     * @return day view for the previous day
     */
    @PostMapping("/day/previous")
    public String seePreviousDay( @RequestParam LocalDate date) {
        LocalDate previousDate = date.minusDays(1);
        return "redirect:/day?date=" + previousDate;
    }


}
