package br.ifsp.covid.persistence;

import br.ifsp.covid.model.Bulletin;

import java.util.List;

public interface BulletinDao {
    boolean insert(Bulletin bulletin) throws DuplicatedBulletinException;
    boolean delete(Bulletin bulletin);
    boolean update(Bulletin bulletin);
    List<Bulletin> findAll();
}
