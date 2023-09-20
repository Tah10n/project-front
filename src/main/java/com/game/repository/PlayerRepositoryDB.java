package com.game.repository;

import com.game.entity.Player;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.jaxb.mapping.spi.NamedQuery;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        sessionFactory = new Configuration().configure()
                                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            Query<Player> query = session.createNativeQuery("select * from rpg.player", Player.class);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    @Override
    public int getAllCount() {
        Integer allCount = 0;
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createNamedQuery("getAllCount");

            allCount = Math.toIntExact((Long) query.uniqueResult()) ;

//            System.out.println("????????????????");
//            System.out.println(allCount);

            //return allCount;
        } catch (Exception e) {
            System.out.println("Ошибка в методе getAllCount");
            e.printStackTrace();
        }
        return allCount;
    }

    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            player.setId(getMaxId() + 1);
            session.persist(player);

            session.getTransaction().commit();
        }
        return player;
    }

    @Override
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(player);

            session.getTransaction().commit();
        }
        return player;
    }

    @Override
    public Optional<Player> findById(long id) {

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Player player = session.find(Player.class,id);

            return Optional.of(player);
        }

    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(player);

            session.getTransaction().commit();
        }

    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }

    private long getMaxId() {
        long maxId = 0;
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createNamedQuery("getMaxId");

            maxId = (Long) query.uniqueResult();

            System.out.println("????????????????");
            System.out.println(maxId);


        } catch (Exception e) {
            System.out.println("Ошибка в методе getMaxId");
            e.printStackTrace();
        }
        return maxId;
    }
}