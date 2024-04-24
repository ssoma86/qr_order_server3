package org.lf.app.models.business.bizclient;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="TMP_BIZ")
@Data
public class BizClient implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = -6338868063646590748L;
	
	@Id
	private String id;
	
	
}
