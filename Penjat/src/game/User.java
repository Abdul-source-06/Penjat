package game;

import java.io.Serializable;

public class User implements Serializable {

	 private String name; 

	  private String user; 

	  private String password;

	  private boolean admin;

	  private int punts;
	
	  public User (String name, String user, String password, boolean admin, int punts) {
		  this.name=name;
		  this.user = user;
		  this.password=password;
		  this.admin=admin;
		  this.punts=punts;
	  }

	//Name
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	//User
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	//Password
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	//Admin
	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	//Punts
	public int getPunts() {
		return punts;
	}

	public void setPunts(int punts) {
		this.punts = punts;
	}
	 	
	@Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", admin=" + admin +
                ", punts=" + punts +
                '}';
    }
	  
	  
	  
}
