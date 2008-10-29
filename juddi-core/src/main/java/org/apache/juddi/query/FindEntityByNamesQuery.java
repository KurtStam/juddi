/*
 * Copyright 2001-2008 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.juddi.query;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.query.util.FindQualifiers;
import org.apache.log4j.Logger;
import org.uddi.api_v3.Name;

/**
 * Returns the list of "entity" keys possessing the Names in the passed Name list.
 * Output is restricted by list of "entity" keys passed in.  If null, all entities are searched.
 * Output is produced by building the appropriate JPA query based on input and find qualifiers.
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class FindEntityByNamesQuery extends EntityQuery {

	private Logger log = Logger.getLogger(FindEntityByNamesQuery.class);

	private String entityName;
	private String entityAlias;
	private String keyName;
	private String entityNameChild;
	private String entityAliasChild;
	private String selectSQL;
	
	public FindEntityByNamesQuery(String entityName, String entityAlias, String keyName, String entityNameChild) {
		this.entityName = entityName;
		this.entityAlias = entityAlias;
		this.keyName = keyName;
		this.entityNameChild = entityNameChild;
		this.entityAliasChild = buildAlias(entityNameChild);
		
		StringBuffer sql = new StringBuffer(200);
		sql.append("select distinct " + entityAlias + "." + keyName + " from " + entityName + " " + entityAlias + " ");
		selectSQL = sql.toString();
	}

	public String getEntityName() {
		return entityName;
	}

	public String getEntityAlias() {
		return entityAlias;
	}

	public String getKeyName() {
		return keyName;
	}

	public String getEntityNameChild() {
		return entityNameChild;
	}
	
	public String getEntityAliasChild() {
		return entityAliasChild;
	}

	public String getSelectSQL() {
		return selectSQL;
	}
	
	
	public List<?> select(EntityManager em, FindQualifiers fq, List<Name> names, List<?> keysIn, DynamicQuery.Parameter... restrictions) {
		// If keysIn is not null and empty, then search is over.
		if ((keysIn != null) && (keysIn.size() == 0))
			return keysIn;
		
		if (names == null || names.size() == 0)
			return keysIn;
		
		DynamicQuery dynamicQry = new DynamicQuery(selectSQL);
		appendConditions(dynamicQry, fq, names);
		if (restrictions != null && restrictions.length > 0)
			dynamicQry.AND().pad().appendGroupedAnd(restrictions);

		// TODO: Break up the IN clause into an amount that is configurable (see JUDDI-146)
		dynamicQry.appendInList(entityAlias + "." + keyName, keysIn);
		
		log.debug(dynamicQry);
		
		Query qry = dynamicQry.buildJPAQuery(em);
		List<?> result = qry.getResultList();
		
		return result;
	}
	
	/*
	 * Appends the conditions to the query based on the name list.  	 
	 */
	public void appendConditions(DynamicQuery qry, FindQualifiers fq, List<Name> names) {

		// Append the necessary tables (one will always be added connecting the entity to its name table).
		appendJoinTables(qry, fq, names);
		qry.AND().pad().openParen().pad();
		
		String namePredicate = DynamicQuery.PREDICATE_EQUALS;
		if (fq.isApproximateMatch()) {
			namePredicate = DynamicQuery.PREDICATE_LIKE;
		}
		
		int count = 0;
		for(Name n : names) {
			String nameTerm = entityAliasChild + ".name";
			String nameValue = n.getValue();
			if (fq.isCaseInsensitiveMatch()) {
				nameTerm = "upper(" + entityAliasChild + ".name)";
				nameValue = n.getValue().toUpperCase();
			}
			if (fq.isApproximateMatch())
				nameValue = nameValue.endsWith(DynamicQuery.WILDCARD)?nameValue:nameValue + DynamicQuery.WILDCARD;
			
			if (n.getLang() == null || n.getLang().length() == 0 ) {
				qry.appendGroupedAnd(new DynamicQuery.Parameter(nameTerm, nameValue, namePredicate));
			}
			else {
				String langValue = n.getLang().endsWith(DynamicQuery.WILDCARD)?n.getLang().toUpperCase():n.getLang().toUpperCase() + DynamicQuery.WILDCARD;
				qry.appendGroupedAnd(new DynamicQuery.Parameter(nameTerm, nameValue, namePredicate), 
									 new DynamicQuery.Parameter("upper(" + entityAliasChild + ".langCode)", langValue, DynamicQuery.PREDICATE_LIKE));
			}
			
			if (count + 1 < names.size())
				qry.OR().pad();
			
			count++;
		}
		qry.closeParen().pad();
		
	}
	
	/*
	 * Appends the necessary join table for the child entity 
	 */
	public void appendJoinTables(DynamicQuery qry, FindQualifiers fq, List<Name> names) {
		qry.comma().pad().append(entityNameChild + " " + entityAliasChild).pad();
		qry.WHERE().pad().openParen().pad();
		qry.append(entityAlias + "." + keyName + " = " + entityAliasChild + ".id." + keyName + " ");
		qry.closeParen().pad();
	}
	
}
