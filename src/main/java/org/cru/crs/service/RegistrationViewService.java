package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.cru.crs.model.RegistrationViewEntity;
import org.sql2o.Connection;

@RequestScoped
public class RegistrationViewService {

	Connection sqlConnection;
	
	public RegistrationViewService() {}
	
	@Inject
	public RegistrationViewService(Connection sqlConnection) {
		this.sqlConnection = sqlConnection;
	}
	
	public RegistrationViewEntity getRegistrationViewById(UUID id) {
		return sqlConnection.createQuery(RegistrationViewQueries.selectById())
								.addParameter("id", id)
								.setAutoDeriveColumnNames(true)
								.executeAndFetchFirst(RegistrationViewEntity.class);
	}
	
	public List<RegistrationViewEntity> getRegistrationViewsForConference(UUID conferenceId) {
		return sqlConnection.createQuery(RegistrationViewQueries.selectByConferenceId())
								.addParameter("conferenceId", conferenceId)
								.setAutoDeriveColumnNames(true)
								.executeAndFetch(RegistrationViewEntity.class);
	}
	
	public void insertRegistrationView(RegistrationViewEntity dataView) {
		sqlConnection.createQuery(RegistrationViewQueries.insert())
								.addParameter("id", dataView.getId())
								.addParameter("conferenceId", dataView.getConferenceId())
								.addParameter("createdByUserId", dataView.getCreatedByUserId())
								.addParameter("name", dataView.getName())
								.addParameter("visibleBlockIds", dataView.getVisibleBlockIds())
								.executeUpdate();
	}
	
	public void updateRegistrationView(RegistrationViewEntity dataView) {
		sqlConnection.createQuery(RegistrationViewQueries.update())
								.addParameter("id", dataView.getId())
								.addParameter("conferenceId", dataView.getConferenceId())
								.addParameter("createdByUserId", dataView.getCreatedByUserId())
								.addParameter("name", dataView.getName())
								.addParameter("visibleBlockIds", dataView.getVisibleBlockIds())
								.executeUpdate();
	}
	
	public void deleteRegistrationView(UUID id) {
		sqlConnection.createQuery(RegistrationViewQueries.delete())
								.addParameter("id", id)
								.executeUpdate();
	}
	
	private static class RegistrationViewQueries {

		private static String selectById() {
			return "SELECT * FROM registration_views WHERE id = :id";
		}
		
		private static String selectByConferenceId() {
			return "SELECT * FROM registration_views WHERE conference_id = :conferenceId";
		}

		private static String update() {
			return "UPDATE registration_views SET conference_id = :conferenceId, created_by_user_id = :createdByUserId, " +
					"name = :name, visible_block_ids = :visibleBlockIds WHERE id = :id";
		}

		private static String insert() {
			return "INSERT INTO registration_views(id, conference_id, created_by_user_id, name, visible_block_ids)" +
					"VALUES(:id, :conferenceId, :createdByUserId, :name, :visibleBlockIds)";
		}

		private static String delete() {
			return "DELETE FROM registration_views WHERE id = :id";
		}
		
	}
	
}
