package accountserver.database;

import com.google.gson.annotations.Expose;
import main.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import utils.IDGenerator;

import javax.persistence.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by xakep666 on 23.10.16.
 *
 * Describes user
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",unique = true,nullable = false)
    private int id;
    @NotNull
    private static String digestAlg = "sha-256";
    @NotNull
    private static Logger log = LogManager.getLogger(User.class);
    @NotNull
    @Column(name = "name",nullable = false)
    @Expose
    private String name;
    @NotNull
    @Column(name = "password",nullable = false)
    private byte[] passwordHash = new byte[0];
    @NotNull
    @Column(name = "email")
    private String email = "";
    @NotNull
    @Column(name = "registration_date",nullable = false)
    @Expose
    private Date registrationDate = new Date();

    static {
        log.info("Hashing passwords with "+digestAlg);
    }

    /**
     * Create new user
     * @param name user name
     * @param password user password
     */
    public User(@NotNull String name, @NotNull String password) {
        this.id= ApplicationContext.instance().get(IDGenerator.class).next();
        this.name = name;
        try {
            MessageDigest md = MessageDigest.getInstance(digestAlg);
            md.update(password.getBytes());
            passwordHash = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);
        }
        log.info(String.format("Created new user %s, id %d",name,id));
    }

    /**
     * @return user`s name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * @return user`s id
     */
    public int getId() {
        return id;
    }

    /**
     * Change user`s name
     * @param newName a new name
     */
    public void setName(@NotNull String newName) {
        log.info("User "+name+" changed name to "+newName);
        name=newName;
    }

    /**
     * Validate given password for user
     * @param password password to check
     * @return true if password is valid for this user, false otherwise
     */
    public boolean validatePassword(@NotNull String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(digestAlg);
            md.update(password.getBytes());
            return MessageDigest.isEqual(passwordHash,md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    /**
     * Set new password for user
     * @param newPassword a new password
     */
    public void updatePassword(@NotNull String newPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance(digestAlg);
            md.update(newPassword.getBytes());
            passwordHash=md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * @return User email
     */
    @NotNull
    public String getEmail() {
        return email;
    }

    /**
     * Update user email
     * @param email new email
     */
    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    /**
     * @return User registration date
     */
    @NotNull
    public Date getRegistrationDate() {
        return registrationDate;
    }

    @Override
    public boolean equals(Object o) {
        return (this==o) || (o instanceof User) &&
                ((User) o).id==id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
