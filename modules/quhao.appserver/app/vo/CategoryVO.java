package vo;

import com.withiter.models.merchant.Category;

public class CategoryVO {

	public long count = 0;
	public String cateType;
	
	public static CategoryVO build(Category c){
		CategoryVO vo = new CategoryVO();
		vo.cateType = c.cateType;
		vo.count = c.count;
		return vo;
	}
}
