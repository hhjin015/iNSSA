import React, { ForwardedRef, forwardRef } from 'react';
import HorizontalLine from '../../../utils/HorizontalLine';
import CommentCard from '../../molecules/CommentCard';
import CommentInput from '../../molecules/CommentInput';
import {
  EachCommentGroup,
  HorizontalLineWrapper,
  ReComments,
  StyledCommentSection,
} from './styles';
import {
  PropTypes,
  CommentResponseTypes,
  ReCommentResponseTypes,
} from './types';

/**
 * PostDetail 페이지의 Comment 섹션
 *
 * @author jojo
 */
const CommentSection = (
  {
    commentResponses,
    inputs,
    commentIdWritingRecomment,
    clickSubmitHandler,
    changeCommentInputHandler,
    ...restHandlers
  }: PropTypes,
  ref: ForwardedRef<HTMLInputElement>,
) => {
  return (
    <StyledCommentSection>
      {commentResponses.map(
        ({
          commentId,
          reCommentResponses,
          ...restComment
        }: CommentResponseTypes) => (
          <EachCommentGroup key={commentId}>
            <HorizontalLineWrapper>
              <HorizontalLine width="90%" />
            </HorizontalLineWrapper>
            <CommentCard
              commentId={commentId}
              isReComment={false}
              commentIdWritingRecomment={commentIdWritingRecomment}
              {...restHandlers}
              {...restComment}
            />
            {reCommentResponses.map(
              ({ reCommentId, ...restRecomment }: ReCommentResponseTypes) => (
                <ReComments key={reCommentId}>
                  <CommentCard
                    commentId={reCommentId}
                    isReComment
                    commentIdWritingRecomment={commentIdWritingRecomment}
                    {...restRecomment}
                    {...restHandlers}
                  />
                </ReComments>
              ),
            )}
          </EachCommentGroup>
        ),
      )}
      <CommentInput
        ref={ref}
        inputs={inputs}
        clickSubmitHandler={clickSubmitHandler}
        changeCommentInputHandler={changeCommentInputHandler}
      />
    </StyledCommentSection>
  );
};

export default forwardRef(CommentSection);