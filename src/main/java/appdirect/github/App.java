package appdirect.github;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.PullRequestService;

public class App {
	private static final String ACCESS_TOKEN = "";
	private static final String OWNER = "";
	private static final String REPO = "";

	private static final String[] HEADERS = { "PR Number", "Created At", "Merged At", "Time to Merge", "Target Branch", "Additions",
			"Deletions", "Changed Files", "Commits", "Comments" };

	public static void main(String[] args) throws IOException {
		System.out.println("Running ...");

		PullRequestService service = new PullRequestService();
		service.getClient().setOAuth2Token(ACCESS_TOKEN);

		RepositoryId repository = new RepositoryId(OWNER, REPO);

		PageIterator<PullRequest> pullRequestPages = service.pagePullRequests(repository, "closed", 100);

		try (PrintWriter writer = new PrintWriter("pull_request_stats.txt")) {
			writer.println(StringUtils.join(HEADERS, ","));

			int i = 0;
			int j = 0;
			int mergedPrs = 0;
			while (pullRequestPages.hasNext() && i < 20) {
				System.out.println(String.format("Processing page: %s", i++));
				Collection<PullRequest> prs = pullRequestPages.next();
				for (PullRequest pr : prs) {
					System.out.println(String.format("Processing PR: %s", j++));
					if (pr.getMergedAt() == null) {
						continue;
					}
					mergedPrs++;
					pr = service.getPullRequest(repository, pr.getNumber());

					List<String> stats = new ArrayList<>();

					Date createdAt = pr.getCreatedAt();
					Date mergedAt = pr.getMergedAt();
					long timeToMerge = mergedAt.getTime() - createdAt.getTime();

					stats.add(String.valueOf(pr.getNumber()));
					stats.add(String.valueOf(createdAt));
					stats.add(String.valueOf(mergedAt));
					stats.add(String.valueOf(timeToMerge));
					stats.add(pr.getBase().getRef());
					stats.add(String.valueOf(pr.getAdditions()));
					stats.add(String.valueOf(pr.getDeletions()));
					stats.add(String.valueOf(pr.getChangedFiles()));
					stats.add(String.valueOf(pr.getCommits()));
					stats.add(String.valueOf(pr.getComments()));
					
					writer.println(StringUtils.join(stats, ","));
				}
			}

			System.out.println(String.format("Number of merged PRs: %s", mergedPrs));
		}
	}
}
