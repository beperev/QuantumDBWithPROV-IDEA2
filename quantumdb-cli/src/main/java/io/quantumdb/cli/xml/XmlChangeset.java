package io.quantumdb.cli.xml;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import lombok.Data;

@Data
public class XmlChangeset {

	private final List<XmlOperation<?>> operations = Lists.newArrayList();
	private String id;
	private String author;
	private String description;

	static XmlChangeset convert(XmlElement element) {
		checkArgument(element.getTag().equals("changeset"));

		XmlChangeset changeset = new XmlChangeset();
		changeset.setId(element.getAttributes().get("id"));
		changeset.setAuthor(element.getAttributes().get("author"));

		for (XmlElement child : element.getChildren()) {
			if (child.getTag().equals("operations")) {
				for (XmlElement subChild : child.getChildren()) {
					changeset.getOperations().add(XmlOperation.convert(subChild));
				}
			}
			else if (child.getTag().equals("description")) {
				String description = child.getChildren().stream()
						.filter(subChild -> subChild.getTag() == null)
						.map(XmlElement::getText)
						.filter(Objects::nonNull)
						.collect(Collectors.joining());

				changeset.setDescription(description);
			}
		}

		return changeset;
	}

}
