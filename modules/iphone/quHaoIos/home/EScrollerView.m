//
//  EScrollerView.m
//  icoiniPad
//
//  Created by Ethan on 12-11-24.
//
//

#import "EScrollerView.h"
@implementation EScrollerView
@synthesize delegate;

- (void)dealloc {
	[scrollView release];
    [noteTitle release];
	delegate=nil;
    if (pageControl) {
        [pageControl release];
    }
    if (imageArray) {
        [imageArray release];
        imageArray=nil;
    }
    if (titleArray) {
        [titleArray release];
        titleArray=nil;
    }
    [super dealloc];
}
-(id)initWithFrameRect:(CGRect)rect ImageArray:(NSArray *)imgArr TitleArray:(NSArray *)titArr
{
    
	if ((self=[super initWithFrame:rect])) {
        self.userInteractionEnabled=YES;
        titleArray=[titArr retain];
        NSMutableArray *tempArray=[NSMutableArray arrayWithArray:imgArr];
        [tempArray insertObject:[imgArr objectAtIndex:([imgArr count]-1)] atIndex:0];
        [tempArray addObject:[imgArr objectAtIndex:0]];
        imageArray=[[NSArray arrayWithArray:tempArray] retain];
		viewSize=rect;
        NSUInteger pageCount=[imageArray count];
        scrollView=[[UIScrollView alloc]initWithFrame:CGRectMake(0, 0, viewSize.size.width, viewSize.size.height)];
        scrollView.pagingEnabled = YES;
        scrollView.contentSize = CGSizeMake(viewSize.size.width * pageCount, viewSize.size.height);
        scrollView.showsHorizontalScrollIndicator = NO;
        scrollView.showsVerticalScrollIndicator = NO;
        scrollView.scrollsToTop = NO;
        scrollView.delegate = self;
        for (int i=0; i<pageCount; i++) {
            NSString *imgURL=[imageArray objectAtIndex:i];
            EGOImageView * imgView = [[[EGOImageView alloc] initWithPlaceholderImage:[UIImage imageNamed:@"no_logo.png"]] autorelease];
            
            if (![[Helper returnUserString:@"showImage"] boolValue]||[imgURL isEqualToString:@""])
            {
                imgView.image = [UIImage imageNamed:@"top"];
            }
            else
            {
                imgView.imageURL =[NSURL URLWithString:[NSString stringWithFormat:@"%@%@",IP,[imgURL    stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding ]]];
            }
            [imgView setFrame:CGRectMake(viewSize.size.width*i, 0,viewSize.size.width, viewSize.size.height)];
            imgView.tag=i;
            UITapGestureRecognizer *Tap =[[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(imagePressed:)] autorelease];
            [Tap setNumberOfTapsRequired:1];
            [Tap setNumberOfTouchesRequired:1];
            imgView.userInteractionEnabled=YES;
            [imgView addGestureRecognizer:Tap];
            [scrollView addSubview:imgView];
        }
        [scrollView setContentOffset:CGPointMake(viewSize.size.width, 0)];
        [self addSubview:scrollView];

        
        
        //说明文字层
        UIView *noteView=[[UIView alloc] initWithFrame:CGRectMake(0, self.bounds.size.height-25,self.bounds.size.width,25)];
        [noteView setBackgroundColor:[UIColor colorWithRed:0.8 green:0.8 blue:0.8 alpha:0]];
        
        float pageControlWidth=(pageCount-2)*10.0f+40.f;
        float pagecontrolHeight=20.0f;
//        pageControl=[[UIPageControl alloc]initWithFrame:CGRectMake((self.frame.size.width-pageControlWidth),6, pageControlWidth, pagecontrolHeight)];
        pageControl=[[UIPageControl alloc]initWithFrame:CGRectMake((self.frame.size.width-pageControlWidth),6, pageControlWidth, pagecontrolHeight)];
        pageControl.currentPage=0;
        pageControl.numberOfPages=(pageCount-2);
        [noteView addSubview:pageControl];
        
//        [self addSubview:pageControl];
//        noteTitle=[[UILabel alloc] initWithFrame:CGRectMake(5, 6, self.frame.size.width-pageControlWidth-15, 20)];
//        [noteTitle setText:[titleArray objectAtIndex:0]];
//        [noteTitle setBackgroundColor:[UIColor clearColor]];
//        [noteTitle setFont:[UIFont systemFontOfSize:13]];
//        [noteView addSubview:noteTitle];
        
        [self addSubview:noteView];
        [noteView release];
        self.animationTimer = [NSTimer scheduledTimerWithTimeInterval:3
                                                               target:self
                                                             selector:@selector(scrollTimer)
                                                             userInfo:nil
                                                              repeats:YES];
	}
	return self;
}

//设置滚动
-(void)scrollTimer{
    timerCount = currentPageIndex+1;
    if (currentPageIndex==([imageArray count]-2)) {
        [scrollView setContentOffset:CGPointMake(viewSize.size.width, 0)];
    }else{
        [scrollView setContentOffset:CGPointMake(kDeviceWidth * timerCount, 0) animated:YES];
    }
}

- (void)scrollViewDidScroll:(UIScrollView *)sender
{
    int page = floor((scrollView.contentOffset.x - kDeviceWidth / 2) / kDeviceWidth) + 1;
    currentPageIndex=page;
       
    pageControl.currentPage=(page-1);
    NSInteger titleIndex=page-1;
    if (titleIndex==[titleArray count]) {
        titleIndex=0;
    }
    if (titleIndex<0) {
        titleIndex=[titleArray count]-1;
    }
    [noteTitle setText:[titleArray objectAtIndex:titleIndex]];
}
- (void)scrollViewDidEndDecelerating:(UIScrollView *)_scrollView
{
    if (currentPageIndex==0) {
      
        [_scrollView setContentOffset:CGPointMake(([imageArray count]-2)*viewSize.size.width, 0)];
    }
    if (currentPageIndex==([imageArray count]-1)) {
       
        [_scrollView setContentOffset:CGPointMake(viewSize.size.width, 0)];
        
    }
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
    [self.animationTimer pauseTimer];
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    [self.animationTimer resumeTimerAfterTimeInterval:3];
}


- (void)imagePressed:(UITapGestureRecognizer *)sender
{

    if ([delegate respondsToSelector:@selector(EScrollerViewDidClicked:)]) {
        [delegate EScrollerViewDidClicked:sender.view.tag];
    }
}

@end
