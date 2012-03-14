package org.amneris.petstore.pages;

import org.apache.tapestry5.annotations.ContentType;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.stat.CollectionStatistics;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;

import java.util.Collection;

/**
 * Page used to see the Hibernate statistics.
 */
@ContentType("text/html")
public class Statistics
{
	@Inject
	private Session session;

	@Property
	private String currentEntityName;

	@Property
	private String currentCollectionRoleName;

	@Property
	private String currentQuery;

	@Property
	private String currentSecondLevelCacheRegionName;

	@Property
	private org.hibernate.stat.Statistics statistics;

	void onActivate()
	{
		this.statistics = this.session.getSessionFactory().getStatistics();
	}

	@SuppressWarnings("unchecked")
	public Collection<ClassMetadata> getAllClassMetadata()
	{
		return this.session.getSessionFactory().getAllClassMetadata().values();
	}

	public EntityStatistics getEntityStatistics()
	{
		return this.statistics.getEntityStatistics(this.currentEntityName);
	}

	public CollectionStatistics getCollectionStatistics()
	{
		return this.statistics.getCollectionStatistics(this.currentCollectionRoleName);
	}

	public QueryStatistics getQueryStatistics()
	{
		return this.statistics.getQueryStatistics(this.currentQuery);
	}

	public SecondLevelCacheStatistics getSecondLevelCacheStatistics()
	{
		return this.statistics
				.getSecondLevelCacheStatistics(this.currentSecondLevelCacheRegionName);
	}
}