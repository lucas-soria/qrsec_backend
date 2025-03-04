package com.lsoria.qrsec.service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.lsoria.qrsec.domain.model.Invite;
import com.lsoria.qrsec.domain.model.Role;
import com.lsoria.qrsec.domain.model.User;
import com.lsoria.qrsec.repository.InviteRepository;
import com.lsoria.qrsec.service.exception.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class InviteService {

    @Autowired
    InviteRepository inviteRepository;

    @Autowired
    UserService userService;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private final Role adminRole = new Role(Role.ADMIN);
    private final Role guardRole = new Role(Role.GUARD);
    private final Role ownerRole = new Role(Role.OWNER);

    public List<Invite> findInvitesByCurrentUser(String username) throws Exception {

        // TODO: String currentlyLoggedInUsername = new SecurityContextUserInfo().getUsername();

        Optional<User> currentUser = userService.findByUsername(username);

        if (currentUser.isEmpty()) {

            throw new NotFoundException("User " + username + " not found");

        }

        User user = currentUser.get();
        Set<Invite> invites = new HashSet<>();

        if (user.getAuthorities().contains(adminRole)) {
            invites.addAll(findAll());
        }
        if (user.getAuthorities().contains(guardRole)) {
            invites.addAll(findAllValidForToday());
        }
        if (user.getAuthorities().contains(ownerRole)) {
            invites.addAll(findAllMyInvites(username));
        }

        return new ArrayList<>(invites);

    }

    public List<Invite> findAll() {

        return inviteRepository.findAll();

    }

    public List<Invite> findAllValidForToday() {

        // TODO: String currentlyLoggedInUsername = new SecurityContextUserInfo().getUsername();

        List<Invite> allInvites = findAll();
        LocalDateTime today = LocalDateTime.now();

        List<Invite> invites = new ArrayList<>();
        for (Invite invite : allInvites) {
            if (validToday(today, invite)) {
                invites.add(invite);
            }
        }

        return invites;

    }

    public List<Invite> findAllMyInvites(String username) throws Exception {

        // TODO: String currentlyLoggedInUsername = new SecurityContextUserInfo().getUsername();

        Optional<User> currentUser = userService.findByUsername(username);
        if (currentUser.isEmpty()) {

            throw new NotFoundException("User " + username + " not found");

        }

        return inviteRepository.findByOwner(currentUser.get());

    }

    public Optional<Invite> findOne(String id) {

        return inviteRepository.findById(id);

    }

    public Invite save(Invite invite, String username) throws Exception {

        // TODO: String currentlyLoggedInUsername = new SecurityContextUserInfo().getUsername();

        Optional<User> currentUser = userService.findByUsername(username);
        if (currentUser.isEmpty()) {

            throw new NotFoundException("User " + username + " not found");

        }

        User user = currentUser.get();
        invite.setOwner(user);
        invite.setGuests(invite.getGuests());

        invite.setDays(invite.getDays());
        invite.setHours(invite.getHours());
        invite.setMaxTimeAllowed(invite.getMaxTimeAllowed());

        invite.setDropsTrueGuest(invite.getDropsTrueGuest());
        invite.setNumberOfPassengers(invite.getNumberOfPassengers());

        LocalDateTime time = LocalDateTime.now();
        invite.setCreatedAt(time);
        invite.setLastModifiedAt(time);

        return inviteRepository.insert(invite);

    }

    public Invite update(Invite oldInvite, Invite updatedInvite) {

        oldInvite.setDescription(updatedInvite.getDescription());

        oldInvite.setGuests(updatedInvite.getGuests());

        oldInvite.setDays(updatedInvite.getDays());
        oldInvite.setHours(updatedInvite.getHours());
        oldInvite.setMaxTimeAllowed(updatedInvite.getMaxTimeAllowed());

        oldInvite.setDropsTrueGuest(updatedInvite.getDropsTrueGuest());
        oldInvite.setNumberOfPassengers(updatedInvite.getNumberOfPassengers());

        oldInvite.setLastModifiedAt(LocalDateTime.now());

        return inviteRepository.save(oldInvite);

    }

    public Boolean delete(String id) throws Exception {

        Optional<Invite> foundInvite = this.findOne(id);
        if (foundInvite.isEmpty()) {

            throw new NotFoundException("Invite " + id + " not found");

        }

        Invite inviteToDelete = foundInvite.get();
        if (!inviteToDelete.getEnabled()) {

            inviteRepository.deleteById(id);

            return true;

        }

        inviteToDelete.setEnabled(false);
        inviteToDelete.setLastModifiedAt(LocalDateTime.now());
        inviteRepository.save(inviteToDelete);

        return false;

    }

    public Boolean inviteIsValid(Invite invite, String timestamp) throws Exception {

        if (!invite.getEnabled()) {
            return false;
        }

        // Convert to Instant (UTC)
        Instant utcInstant = OffsetDateTime.parse(timestamp).toInstant();
        // Convert to LocalDateTime in UTC, needed to compare timestamp with timestamps generated by the server
        LocalDateTime utcLocalTimestamp = LocalDateTime.ofInstant(utcInstant, ZoneId.of("UTC"));

        // If the person wants to get in before the invite was created -> invalid request
        if (utcLocalTimestamp.isBefore(invite.getCreatedAt())) {
            return false;
        }

        // If the person has left and the time they want to get in is before the time they left  -> invalid invite
        if (invite.getDepartureTime() != null && utcLocalTimestamp.isBefore(invite.getDepartureTime())) {
            return false;
        }

        // Parse timestamp as LocalDateTime needed to compare user time with invite's config
        LocalDateTime timestampLocal = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
        if (!validToday(timestampLocal, invite)) {
            return false;
        }

        LocalTime timeFromTimestamp = timestampLocal.toLocalTime();

        List<List<String>> inviteHours = invite.getHours();
        boolean isInBetween = inviteHours.isEmpty(); // If the list is empty it is a valid invite, no need to check
        for (int i = 0; i < inviteHours.size(); i++) {
            String startTime = invite.getHours().get(i).get(0);
            String endTime = invite.getHours().get(i).get(1);

            LocalTime parsedStartTime = LocalTime.parse(startTime, timeFormatter);
            LocalTime parsedEndTime = LocalTime.parse(endTime, timeFormatter);

            if (timeFromTimestamp.isAfter(parsedStartTime) && timeFromTimestamp.isBefore(parsedEndTime)) {
                isInBetween = true;
                break;
            }
        }

        return isInBetween;

    }

    private Boolean validToday(LocalDateTime timestamp, Invite invite) {

        DayOfWeek day = timestamp.getDayOfWeek();
        int dayAsInt = day.getValue();
        if (dayAsInt == 7) {
            dayAsInt = 0;
        }

        Set<String> inviteDays = invite.getDays();

        return inviteDays.isEmpty() || inviteDays.contains(Integer.toString(dayAsInt));

    }

    public Invite doAction(Invite invite, String action, LocalDateTime timestamp) throws Exception {

        switch (action) {
            case "arrival":
                invite.setArrivalTime(timestamp);
                break;
            case "departure":
                invite.setDepartureTime(timestamp);
                break;
            case "enable":
                invite.setEnabled(true);
                break;
            case "disable":
                invite.setEnabled(false);
                break;
            default:
                throw new NotFoundException("Action " + action + " invalid");
        }

        invite.setLastModifiedAt(LocalDateTime.now());
        this.inviteRepository.save(invite);

        return invite;

    }

}
