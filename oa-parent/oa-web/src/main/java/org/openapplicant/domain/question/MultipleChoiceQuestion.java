package org.openapplicant.domain.question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.validator.AssertTrue;
import org.openapplicant.policy.AssertNotFrozen;
import org.springframework.util.Assert;


@Entity
public class MultipleChoiceQuestion extends Question {

	private List<String> choices = new ArrayList<String>();
	
	private Integer answerIndex;

	//FIXME: this is a hack so we dont have to deal with adding/removing questions now.
	public MultipleChoiceQuestion() {
		choices.add("Choice 1");
		choices.add("Choice 2");
		choices.add("Choice 3");
		choices.add("Choice 4");
	}
		
	@Transient
	public List<String> getChoices() {
		return Collections.unmodifiableList(choices);
	}
	
	public void setChoiceText(int index, String text) {
		Assert.isTrue(0 <= index && index < choices.size(), "Index out of bounds");
		getChoicesInternal().set(index, text);
	}
	
	/**
	 * This only copies over fields according to the prior size
	 * of choices. Eg.
	 */
	public void setChoicesText(List<String> values) {
		for(int i=0; i<choices.size(); i++) {
			setChoiceText(i, values.get(i));
		}
	}
	
	@Column
	@CollectionOfElements
	private List<String> getChoicesInternal() {
		return choices;
	}

	private void setChoicesInternal(List<String> values) {
		this.choices = values;
	}
	
	@Transient
	@AssertTrue(message="choice cannot be blank")
	public boolean isChoicesValid() {
		for(String each : choices) {
			if(StringUtils.isBlank(each)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @return this question's answer
	 */
	@Column
	public Integer getAnswerIndex() {
		return answerIndex;
	}
	
	@AssertNotFrozen
	public void setAnswerIndex(Integer answer) {
		this.answerIndex = answer;
	}
	
	@Transient
	public String getAnswer() {
		if( null != answerIndex &&
			0 <= answerIndex &&
			choices.size() > answerIndex) {
			return choices.get(answerIndex);
		} else {
			return "";
		}
	}
	
	/**
	 * @see Question#createSnapshot()
	 */
	@Override
	protected Question createNewInstance() {
		return new MultipleChoiceQuestion();
	}
	
	/**
	 * @see Question#merge(Question)
	 */
	@Override
	public void doMerge(Question other) {
		
		if(!(other instanceof MultipleChoiceQuestion))
			throw new IllegalStateException();
		
		MultipleChoiceQuestion multipleChoice = (MultipleChoiceQuestion) other;
		
		setChoicesText(multipleChoice.getChoices());
		answerIndex = multipleChoice.getAnswerIndex();
	}
	
	@Override
	public void accept(IQuestionVisitor visitor) {
		visitor.visit(this);
	}
}
