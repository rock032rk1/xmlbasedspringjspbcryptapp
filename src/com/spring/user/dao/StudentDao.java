package com.spring.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spring.user.pojo.Student;
@Component
public class StudentDao {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private SessionFactory sessionFactory;
	
	@Transactional
	public int save(Student s) {
		Session ses=sessionFactory.getCurrentSession();
		s.setPassword(bCryptPasswordEncoder.encode(s.getPassword()));
		ses.save(s);
		
		return s.getSid();
	}
	
	public Student findByUsername(String username) throws Exception {
		Student s=null;
		Class.forName("com.mysql.jdbc.Driver");
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root","root");
		
		PreparedStatement ps=con.prepareStatement("select * from student_bcrypt_demo where username=?");
		ps.setString(1,username);
		
		ResultSet rs=ps.executeQuery();
		
		while(rs.next()) {
			
			s=new Student();
			s.setSid(rs.getInt("sid"));
			s.setName(rs.getString(2));
			s.setPassword(rs.getString(3));
			s.setUsername(rs.getString(4));
			
		}
		
		return s;
	}
	@Transactional
	public Student login(String username,String password) throws Exception {
		Student s=null;
		
		StudentDao sd=new StudentDao();
		s=sd.findByUsername(username);
		
		if(s!=null) {
			if(bCryptPasswordEncoder.matches(password, s.getPassword())) {
				return s;
			}
		}
		
		return s;
	}
}
