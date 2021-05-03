package com.demo.query.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.demo.query.GiftCardEntity;

// @RepositoryRestResource(collectionResourceRel = "cards", path = "cards")
public interface GiftCardRepository extends MongoRepository<GiftCardEntity, String> {
  //
  // @RestResource(exported = false)
  // @Override
  // void deleteById(String aLong);
  //
  // @RestResource(exported = false)
  // @Override
  // void delete(GiftCardEntity entity);
  //
  // @RestResource(exported = false)
  // @Override
  // void deleteAll(Iterable<? extends GiftCardEntity> entities);
  //
  // @RestResource(exported = false)
  // @Override
  // void deleteAll();
  //
  // @RestResource(exported = false)
  // @Override
  // <S extends GiftCardEntity> S save(S entity);
  //
  // @RestResource(exported = false)
  // @Override
  // <S extends GiftCardEntity> List<S> saveAll(Iterable<S> entities);

}
