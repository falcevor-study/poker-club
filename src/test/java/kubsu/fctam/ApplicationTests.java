package kubsu.fctam;

import kubsu.fctam.cardComparator.CombinationRecognizer;
import kubsu.fctam.entity.Card;
import kubsu.fctam.service.CardService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.criteria.CriteriaBuilder;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private CardService service;

	@Autowired
	@Spy
	private CombinationRecognizer recognizer;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test() throws Exception {
//		int[] elements = new int[] {11, 23, 45, 32, 43, 5, 6, 3, 8, 51};
//		doReturn(null).when(recognizer).findStraightFlush();
		int[] elements = new int[] {3, 16, 5, 6, 7, 8, 9};
		List<ArrayList<Integer>> combinations = getCombinations(elements, 7);
		for (ArrayList<Integer> combination : combinations) {
			for (Integer integer : combination) {
				Card card = service.get(elements[integer]);
				System.out.println("id: " + card.getId() + "\t\t\tsuit: " + card.getSuit() + "\t\t\tvalue: " + card.getValue() + "\t\t\trate: " + card.getRate());
			}
			recognizer.setCards(Arrays.asList(service.get(elements[combination.get(0)]),
					                          service.get(elements[combination.get(1)]),
					                          service.get(elements[combination.get(2)]),
					                          service.get(elements[combination.get(3)]),
					                          service.get(elements[combination.get(4)]),
					                          service.get(elements[combination.get(5)]),
					                          service.get(elements[combination.get(6)])));
			System.out.println(recognizer.getCombination().toString());
		}
	}

	private List<ArrayList<Integer>> getCombinations(int[] elements, int k) {
		ArrayList<ArrayList<Integer>> combList = new ArrayList<>();

		// get the length of the array
		// e.g. for {'A','B','C','D'} => n = 4
		int n = elements.length;

		if(k > n){
			System.out.println("Invalid input, k > n");
			return null;
		}

		// get the combination by index
		// e.g. 01 --> AB , 23 --> CD
		List<Integer> combination = Arrays.asList(new Integer[k]);

		// position of current index
		//  if (r = 1)              r*
		//  index ==>        0   |   1   |   2
		//  element ==>      A   |   B   |   C
		int r = 0;
		int index = 0;

		while(r >= 0){
			// possible indexes for 1st position "r=0" are "0,1,2" --> "A,B,C"
			// possible indexes for 2nd position "r=1" are "1,2,3" --> "B,C,D"

			// for r = 0 ==> index < (4+ (0 - 2)) = 2
			if(index <= (n + (r - k))){
				combination.set(r, index);

				// if we are at the last position print and increase the index
				if(r == k-1){

					//do something with the combination e.g. add to list or print
					combList.add(new ArrayList<>(combination));
					if (combList.size() == 1000)
						return combList;
					index++;
				}
				else{
					// select index for next position
					index = combination.get(r)+1;
					r++;
				}
			}
			else{
				r--;
				if(r > 0)
					index = combination.get(r)+1;
				else
					index = combination.get(0)+1;
			}
		}
		return combList;
	}
}
