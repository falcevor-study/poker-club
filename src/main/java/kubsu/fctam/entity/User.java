package kubsu.fctam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "tUser")
public class User {
    @Id
    @GeneratedValue
    private int id;

    @Column(length = 100, unique = true)
    private String login;

    @Column(length = 1000)
    private String password;

    @Column
    private int money;


    public User() {}

    public User(String login, String password, int money) {
        this.login = login;
        this.password = password;
        this.money = money;
    }


    public void setLogin(String login) { this.login = login; }
    public void setPassword(String password) { this.password = password; }
    public void setMoney(int money) { this.money = money; }

    public int getId() { return id; }
    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public int getMoney() { return money; }

    @Override
    public boolean equals(Object another) {
        return another instanceof User
                && ((User)another).getLogin().equals(this.login);
    }
}
