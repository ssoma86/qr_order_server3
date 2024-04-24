package org.lf.app.models;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * JpaRepository 상속 해서 공통 부분 설정
 * 
 * @author LF
 * 
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
	/**
	 	IsNotNull		findByAgeNotNull		...  where x.age not null
		Like			findByNameLike			...  where x.name like ?1
		NotLike			findByNameNotLike		...  where x.name not like ?1
		StartingWith	findByNameStartingWith	...  where x.name like ?1(parameter bound with appended %)
		EndingWith		findByNameEndingWith	...  where x.name like ?1(parameter bound with prepended %)
		Containing		findByNameContaining	...  where x.name like ?1(parameter bound wrapped in %)
		OrderBy			findByAgeOrderByName	...  where x.age = ?1 order by x.name desc
		Not				findByNameNot			...  where x.name <> ?1
		In				findByAgeIn				...  where x.age in ?1
		NotIn			findByAgeNotIn			...  where x.age not in ?1
		True			findByActiveTrue		...  where x.avtive = true
		Flase			findByActiveFalse		...  where x.active = false
		And 			findByNameAndAge		...  where x.name = ?1 and x.age = ?2
		Or				findByNameOrAge			...  where x.name = ?1 or x.age = ?2
		Between			findBtAgeBetween		...  where x.age between ?1 and ?2
		LessThan		findByAgeLessThan		...  where x.age  <  ?1
		GreaterThan		findByAgeGreaterThan	...  where x.age > ?1
		After/Before	...	...
		IsNull			findByAgeIsNull			...  where x.age is null
	 */
}