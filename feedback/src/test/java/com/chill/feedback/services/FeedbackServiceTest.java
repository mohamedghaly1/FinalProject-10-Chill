//package com.chill.feedback.services;
//
//import com.chill.feedback.command.FeedbackCommand;
//import com.chill.feedback.command.FeedbackCommandDispatcher;
//import com.chill.feedback.factories.FeedbackFactory;
//
//import com.chill.feedback.factories.ReviewFactory;
//import com.chill.feedback.models.Thread;
//import com.chill.feedback.repositories.FeedbackRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.*;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//import com.chill.feedback.models.*;
//
//@ExtendWith(MockitoExtension.class)
//class FeedbackServiceTest {
//
//    @Mock
//    private FeedbackRepository repo;
//
//    @Mock
//    private FeedbackCommand upvoteCmd;
//
//    @Mock
//    private FeedbackCommand downvoteCmd;
//
//    private FeedbackService service;
//
//    private Review sampleReview;
//    private final UUID feedbackId = UUID.randomUUID();
//    private final UUID userId     = UUID.randomUUID();
//    private final Feedback fakeFeedback = new Feedback() {{ setId(feedbackId); }};
//
//    @BeforeEach
//    void setUp() {
//
//		when(upvoteCmd.getName()).thenReturn("upvote");
//		when(downvoteCmd.getName()).thenReturn("downvote");
//		Map<String,FeedbackCommand> feedbackCommandMap = Map.of(
//				"upvote",   upvoteCmd,
//				"downvote", downvoteCmd
//		);
//		FeedbackCommandDispatcher dispatcher =
//				new FeedbackCommandDispatcher(List.of(upvoteCmd, downvoteCmd));
//		service = new FeedbackService(repo, dispatcher);
//		sampleReview = new Review();
//        sampleReview.setId(feedbackId);
//        sampleReview.setComment("Great ride!");
//        sampleReview.setRating(5);
//    }
//
//	@Test
//	void createFeedback_success() {
//        // tell Mongo to echo back whatever you save
//        when(repo.save(any(Review.class))).thenReturn(sampleReview);
//
//        Feedback result = service.createFeedback(sampleReview);
//
//        verify(repo).save(any(Review.class));
//
//        assertThat(result).isSameAs(sampleReview);
//	}
//
//    @Test
//    void createFeedback_unsupportedType() {
//        // e.g. pass in a raw Feedback anonymous subclass
//        Feedback weird = new Feedback(){{ setComment("x"); }};
//        assertThatThrownBy(() -> service.createFeedback(weird))
//                .isInstanceOf(ResponseStatusException.class)
//                .hasMessageContaining("can not be created");
//    }
//
//	@Test
//	void getFeedbackById_found() {
//		when(repo.findById(sampleReview.getId()))
//				.thenReturn(Optional.of(sampleReview));
//
//		Feedback result = service.getFeedbackById(sampleReview.getId());
//
//		assertThat(result).isSameAs(sampleReview);
//	}
//
//	@Test
//	void getAllOfType_returnsOnlyThatSubtype() {
//		// ─── Arrange ──────────────────────────────────────────────────────────────
//		Review r1 = new Review(); r1.setId(UUID.randomUUID()); r1.setComment("r1");r1.setRating(5);
//		Review r2 = new Review(); r2.setId(UUID.randomUUID()); r2.setComment("r2");r2.setRating(4);
//
//		// tell the mocked repository exactly what to return for this call
//		when(repo.findByType(Review.class)).thenReturn(List.of(r1, r2));
//
//		List<Review> result = service.getAllOfType(Review.class);
//
//		assertThat(result)
//				.hasSize(2)
//				.containsExactly(r1, r2);
//
//		verify(repo).findByType(Review.class);
//	}
//
//	@Test
//	@SuppressWarnings("unchecked")
//	void getAllOfType_nonFeedbackClass_throwsBadRequest() {
//		Class invalid = String.class;
//		assertThatThrownBy(() -> service.getAllOfType(invalid))
//				.isInstanceOf(ResponseStatusException.class)
//				.hasMessageContaining("Invalid feedback type");
//		verify(repo, never()).findByType(any());
//	}
//
//
//    @Test
//    void getComplaintsForVendor_returnsOnlyThatVendor() {
//        UUID vendorId = UUID.randomUUID();
//
//        Complaint c1 = new Complaint();
//        c1.setId(UUID.randomUUID());
//        c1.setVendorId(vendorId);
//
//        Complaint c2 = new Complaint();
//        c2.setId(UUID.randomUUID());
//        c2.setVendorId(vendorId);
//
//        // stub the repo
//        when(repo.findComplaintsByVendorId(vendorId))
//                .thenReturn(List.of(c1, c2));
//
//        List<Complaint> result = service.getComplaintsForVendor(vendorId);
//
//        assertThat(result)
//                .hasSize(2)
//                .containsExactly(c1, c2);
//        verify(repo).findComplaintsByVendorId(vendorId);
//    }
//
//    @Test
//    void getComplaintsForUser_returnsOnlyThatUser() {
//        UUID userId = UUID.randomUUID();
//
//        Complaint c1 = new Complaint();
//        c1.setId(UUID.randomUUID());
//        c1.setUserId(userId);
//
//        // stub the repo
//        when(repo.findComplaintsByUserId(userId))
//                .thenReturn(List.of(c1));
//
//        List<Complaint> result = service.getComplaintsForUser(userId);
//
//        assertThat(result)
//                .hasSize(1)
//                .containsExactly(c1);
//        verify(repo).findComplaintsByUserId(userId);
//    }
//
//	@Test
//	void getFeedbackById_notFound()
//	{
//		when(repo.findById(sampleReview.getId()))
//				.thenReturn(Optional.empty());
//
//		assertThatThrownBy(() -> service.getFeedbackById(sampleReview.getId()))
//				.isInstanceOf(ResponseStatusException.class)
//				.hasMessageContaining("Feedback not found");
//	}
//
//	@Test
//	void updateFeedback_changesCommentAndRating() {
//		Review updated = new Review();
//		updated.setId(sampleReview.getId());
//		updated.setComment("Updated comment");
//		updated.setRating(4);
//
//		when(repo.findById(sampleReview.getId()))
//				.thenReturn(Optional.of(sampleReview));
//		when(repo.save(sampleReview)).thenReturn(sampleReview);
//
//		Feedback result = service.updateFeedback(updated);
//
//		assertThat(sampleReview.getComment()).isEqualTo("Updated comment");
//		assertThat(((Review)sampleReview).getRating()).isEqualTo(4);
//		assertThat(result).isSameAs(sampleReview);
//	}
//
//	@Test
//	void updateFeedback_notFound()
//	{
//		UUID id = UUID.randomUUID();
//		Review incoming = new Review();
//		incoming.setId(id);
//		incoming.setComment("Should fail");
//		incoming.setRating(3);
//		when(repo.findById(id)).thenReturn(Optional.empty());   // DB says “not found”
//		assertThatThrownBy(() -> service.updateFeedback(incoming))
//				.isInstanceOf(ResponseStatusException.class)
//				.hasMessageContaining("Feedback not found");
//		verify(repo, never()).save(any());
//	}
//
//	@Test
//	void deleteFeedbackById_success() {
//		when(repo.findById(sampleReview.getId()))
//				.thenReturn(Optional.of(sampleReview));
//
//		Feedback deleted = service.deleteFeedbackById(sampleReview.getId());
//
//		verify(repo).delete(sampleReview);
//		assertThat(deleted).isSameAs(sampleReview);
//	}
//
//	@Test
//	void deleteFeedbackById_notFound() {
//		UUID id = UUID.randomUUID();
//
//		when(repo.findById(id)).thenReturn(Optional.empty());
//
//		assertThatThrownBy(() -> service.deleteFeedbackById(id))
//				.isInstanceOf(ResponseStatusException.class)
//				.hasMessageContaining("Feedback not found");
//
//		verify(repo, never()).delete(any());
//	}
//
//
//    @Test
//    void upvoteFeedback_delegatesToUpvoteCommand() {
//        when(upvoteCmd.execute(feedbackId, userId)).thenReturn(fakeFeedback);
//        Feedback result = service.upvoteFeedback(feedbackId, userId);
//        assertThat(result).isSameAs(fakeFeedback);
//        verify(upvoteCmd).execute(feedbackId, userId);
////        verifyNoInteractions(downvoteCmd);
//		verify(downvoteCmd, never()).execute(any(), any());
//    }
//
//    @Test
//    void downvoteFeedback_delegatesToDownvoteCommand() {
//        when(downvoteCmd.execute(feedbackId, userId)).thenReturn(fakeFeedback);
//
//        Feedback result = service.downvoteFeedback(feedbackId, userId);
//
//        assertThat(result).isSameAs(fakeFeedback);
//        verify(downvoteCmd).execute(feedbackId, userId);
////        verifyNoInteractions(upvoteCmd);
//		verify(upvoteCmd, never()).execute(any(), any());
//    }
//
//    @Test
//    void unknownCommand_throwsBadRequest() {
//        FeedbackService emptySvc = new FeedbackService(repo, new FeedbackCommandDispatcher(List.of()));
//        assertThatThrownBy(() -> emptySvc.upvoteFeedback(feedbackId, userId))
//                .isInstanceOf(ResponseStatusException.class)
//                .hasMessageContaining("Unknown command");
//    }
//
//    @Test
//    void replyToFeedback_success() {
//        UUID parentId = UUID.randomUUID();
//        Thread parent = new Thread();
//        parent.setId(parentId);
//
//        Thread reply = new Thread();
//        reply.setComment("This is a reply");
//
//        when(repo.findById(parentId)).thenReturn(Optional.of(parent));
//        when(repo.save(reply)).thenReturn(reply);
//
//        // Act
//        Feedback result = service.replyToFeedback(parentId, reply);
//
//        // Assert
//        assertThat(result).isSameAs(reply);
//        verify(repo).save(reply);
//    }
//
//
//    @Test
//    void replyToFeedback_parentNotReplyable_throwsBadRequest() {
//        // Arrange: parent is a Review (not replyable)
//        UUID parentId = UUID.randomUUID();
//        Review parent = new Review();
//        parent.setId(parentId);
//
//        Thread reply = new Thread();  // payload would be ignored
//
//        when(repo.findById(parentId)).thenReturn(Optional.of(parent));
//
//        // Act & Assert
//        assertThatThrownBy(() -> service.replyToFeedback(parentId, reply))
//                .isInstanceOf(ResponseStatusException.class)
//                .hasMessageContaining("Cannot reply to feedback of type");
//        verify(repo, never()).save(any());
//    }
//
//    @Test
//    void getTopReviewsForVendor_returnsSortedList() {
//        UUID vendorId = UUID.randomUUID();
//
//        Review r1 = new Review();
//        r1.setId(UUID.randomUUID());
//        r1.setVendorId(vendorId);
//        r1.setRating(3);
//
//        Review r2 = new Review();
//        r2.setId(UUID.randomUUID());
//        r2.setVendorId(vendorId);
//        r2.setRating(5);
//
//        when(repo.findByVendorIdOrderByRatingDesc(vendorId))
//                .thenReturn(List.of(r2, r1));
//
//        List<Review> result = service.getTopReviewsForVendor(vendorId);
//
//        assertThat(result)
//                .hasSize(2)
//                .containsExactly(r2, r1);
//
//        verify(repo).findByVendorIdOrderByRatingDesc(vendorId);
//    }
//
//    @Test
//    void getTopReviewsForVendor_noReviews_returnsEmpty() {
//        UUID vendorId = UUID.randomUUID();
//        when(repo.findByVendorIdOrderByRatingDesc(vendorId))
//                .thenReturn(Collections.emptyList());
//
//        List<Review> result = service.getTopReviewsForVendor(vendorId);
//
//        assertThat(result).isEmpty();
//        verify(repo).findByVendorIdOrderByRatingDesc(vendorId);
//    }
//
//    @Test
//    void getLeastReviewsForVendor_returnsSortedList() {
//        UUID vendorId = UUID.randomUUID();
//
//        Review r1 = new Review();
//        r1.setId(UUID.randomUUID());
//        r1.setVendorId(vendorId);
//        r1.setRating(1);
//
//        Review r2 = new Review();
//        r2.setId(UUID.randomUUID());
//        r2.setVendorId(vendorId);
//        r2.setRating(4);
//
//        when(repo.findByVendorIdOrderByRatingAsc(vendorId))
//                .thenReturn(List.of(r1, r2));
//
//        List<Review> result = service.getLeastReviewsForVendor(vendorId);
//
//        assertThat(result)
//                .hasSize(2)
//                .containsExactly(r1, r2);
//        verify(repo).findByVendorIdOrderByRatingAsc(vendorId);
//    }
//
//    @Test
//    void getLeastReviewsForVendor_noReviews_returnsEmpty() {
//        UUID vendorId = UUID.randomUUID();
//        when(repo.findByVendorIdOrderByRatingAsc(vendorId))
//                .thenReturn(Collections.emptyList());
//
//        List<Review> result = service.getLeastReviewsForVendor(vendorId);
//
//        assertThat(result).isEmpty();
//        verify(repo).findByVendorIdOrderByRatingAsc(vendorId);
//    }
//
//    @Test
//    void getReviewsByUser_returnsOnlyThatUser() {
//        UUID userId1 = UUID.randomUUID();
//        UUID userId2 = UUID.randomUUID();
//
//        Review r1 = new Review();
//        r1.setId(UUID.randomUUID());
//        r1.setUserId(userId);
//
//        Review r2 = new Review();
//        r2.setId(UUID.randomUUID());
//        r2.setUserId(userId);
//
//        Review r3 = new Review();
//        r3.setId(UUID.randomUUID());
//        r3.setUserId(userId2);
//
//        when(repo.findReviewsByUserId(userId))
//                .thenReturn(List.of(r1, r2));
//
//        List<Review> result = service.getReviewsByUser(userId);
//
//        assertThat(result)
//                .hasSize(2)
//                .containsExactly(r1, r2);
//        verify(repo).findReviewsByUserId(userId);
//    }
//
//    @Test
//    void getTopReviewsByUser_returnsSortedList() {
//        UUID userId = UUID.randomUUID();
//
//        Review low = new Review();
//        low.setId(UUID.randomUUID());
//        low.setUserId(userId);
//        low.setRating(2);
//
//        Review high = new Review();
//        high.setId(UUID.randomUUID());
//        high.setUserId(userId);
//        high.setRating(5);
//
//        when(repo.findReviewsByUserIdOrderByRatingDesc(userId))
//                .thenReturn(List.of(high, low));
//
//        List<Review> result = service.getTopReviewsByUser(userId);
//
//        assertThat(result)
//                .hasSize(2)
//                .containsExactly(high, low);
//        verify(repo).findReviewsByUserIdOrderByRatingDesc(userId);
//    }
//
//    @Test
//    void getTopReviewsByUser_noReviews_returnsEmpty() {
//        UUID userId = UUID.randomUUID();
//        when(repo.findReviewsByUserIdOrderByRatingDesc(userId))
//                .thenReturn(Collections.emptyList());
//
//        List<Review> result = service.getTopReviewsByUser(userId);
//
//        assertThat(result).isEmpty();
//        verify(repo).findReviewsByUserIdOrderByRatingDesc(userId);
//    }
//
//    @Test
//    void getLeastReviewsByUser_returnsSortedList() {
//        UUID userId = UUID.randomUUID();
//
//        Review low = new Review();
//        low.setId(UUID.randomUUID());
//        low.setUserId(userId);
//        low.setRating(1);
//
//        Review high = new Review();
//        high.setId(UUID.randomUUID());
//        high.setUserId(userId);
//        high.setRating(4);
//
//        when(repo.findReviewsByUserIdOrderByRatingAsc(userId))
//                .thenReturn(List.of(low, high));
//
//        List<Review> result = service.getLeastReviewsByUser(userId);
//
//        assertThat(result)
//                .hasSize(2)
//                .containsExactly(low, high);
//        verify(repo).findReviewsByUserIdOrderByRatingAsc(userId);
//    }
//
//    @Test
//    void getLeastReviewsByUser_noReviews_returnsEmpty() {
//        UUID userId = UUID.randomUUID();
//        when(repo.findReviewsByUserIdOrderByRatingAsc(userId))
//                .thenReturn(Collections.emptyList());
//
//        List<Review> result = service.getLeastReviewsByUser(userId);
//
//        assertThat(result).isEmpty();
//        verify(repo).findReviewsByUserIdOrderByRatingAsc(userId);
//    }
//
//    @Test
//    void getRootThreadsByUser_returnsOnlyRootThreads() {
//        UUID userId = UUID.randomUUID();
//
//        Thread t1 = new Thread();
//        t1.setId(UUID.randomUUID());
//        t1.setUserId(userId);
//        t1.setParentId(null);
//
//        Thread t2 = new Thread();
//        t2.setId(UUID.randomUUID());
//        t2.setUserId(userId);
//        t2.setParentId(null);
//
//        Thread t3 = new Thread();
//        t3.setId(UUID.randomUUID());
//        t3.setUserId(userId);
//        t3.setParentId(t2.getId());
//
//        when(repo.findRootThreadsByUserId(userId))
//                .thenReturn(List.of(t1, t2));
//
//        List<Thread> result = service.getRootThreadsByUser(userId);
//
//        assertThat(result)
//                .hasSize(2)
//                .containsExactly(t1, t2);
//        verify(repo).findRootThreadsByUserId(userId);
//    }
//
//    @Test
//    void getRootThreadsByUser_noThreads_returnsEmpty() {
//        UUID userId = UUID.randomUUID();
//        when(repo.findRootThreadsByUserId(userId))
//                .thenReturn(Collections.emptyList());
//
//        List<Thread> result = service.getRootThreadsByUser(userId);
//
//        assertThat(result).isEmpty();
//        verify(repo).findRootThreadsByUserId(userId);
//    }
//
//    @Test
//    void getSubThreadsByUser_returnsOnlySubThreads() {
//        UUID userId = UUID.randomUUID();
//
//        Thread t1 = new Thread();
//        t1.setId(UUID.randomUUID());
//        t1.setUserId(userId);
//        t1.setParentId(UUID.randomUUID());
//
//        Thread t2 = new Thread();
//        t2.setId(UUID.randomUUID());
//        t2.setUserId(userId);
//        t2.setParentId(null);
//
//        when(repo.findSubThreadsByUserId(userId))
//                .thenReturn(List.of(t1));
//
//        List<Thread> result = service.getSubThreadsByUser(userId);
//
//        assertThat(result)
//                .hasSize(1)
//                .containsExactly(t1);
//        verify(repo).findSubThreadsByUserId(userId);
//    }
//
//    @Test
//    void getSubThreadsByUser_noThreads_returnsEmpty() {
//        UUID userId = UUID.randomUUID();
//        when(repo.findSubThreadsByUserId(userId))
//                .thenReturn(Collections.emptyList());
//
//        List<Thread> result = service.getSubThreadsByUser(userId);
//
//        assertThat(result).isEmpty();
//        verify(repo).findSubThreadsByUserId(userId);
//    }
//}
