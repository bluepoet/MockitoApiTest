package com.bluepoet.test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class MockitoApiTest {
	private List mockList;

	@Mock
	private List annotaionMockList;

	@Spy
	private List spyList = new ArrayList<String>();

	@Mock
	private PersonRepository personRepository;

	@InjectMocks
	private PersonService personService = new PersonService();

	@Before
	public void setUp() {
		mockList = mock(LinkedList.class);
	}

	@Test
	public void apiTest_1번() {
		// Given
		// When
		mockList.add("one");
		mockList.add("two");

		// Then
		verify(mockList).add("one");
		verify(mockList).add("two");
	}

	@Test
	public void apiTest_2번() {
		// Given
		when(mockList.get(0)).thenReturn("first");
		when(mockList.get(1)).thenThrow(new RuntimeException());

		// When
		System.out.println(mockList.get(0));
		// System.out.println(mockList.get(1));
		System.out.println(mockList.get(999));

		// Mock객체에 Element를 넣어도 넣은 Element값으로 검증되지 않는다.
		mockList.add(0, "22");

		// Then
		verify(mockList).get(0);
		// Stubbing된 행위에 대한 검증은 의미없다.
		assertThat((String) mockList.get(0), is("first"));
	}

	@Test
	public void apiTest_3번() {
		// Given
		when(mockList.get(anyInt())).thenReturn("element");
		when(mockList.addAll(argThat(new IsListOfTwoElements()))).thenReturn(true);

		// When
		System.out.println(mockList.get(999));
		boolean result = mockList.addAll(Arrays.asList("one", "two"));

		// Then
		verify(mockList).get(anyInt());
		verify(mockList).addAll(argThat(new IsListOfTwoElements()));
		assertThat(result, is(true));
	}

	class IsListOfTwoElements extends ArgumentMatcher<List> {
		@Override
		public boolean matches(Object list) {
			return ((List) list).size() == 2;
		}
	}

	@Test
	public void apiTest_4번() {
		// Given
		// When
		mockList.add("once");
		mockList.add("twice");
		mockList.add("twice");
		mockList.add("three times");
		mockList.add("three times");
		mockList.add("three times");

		// Then
		verify(mockList).add("once");
		verify(mockList, times(1)).add("once");
		verify(mockList, times(2)).add("twice");
		verify(mockList, times(3)).add("three times");
		verify(mockList, never()).add("never happend");
		verify(mockList, atLeastOnce()).add("three times");
		// 최소 2번 이상 호출되었는지 검증
		verify(mockList, atLeast(2)).add("three times");
		// 최대 3번 이하로 호출되었는지 검증
		verify(mockList, atMost(3)).add("three times");
	}

	@Test(expected = RuntimeException.class)
	public void apiTest_5번() {
		// Given
		doThrow(new RuntimeException()).when(mockList).clear();

		// When
		mockList.clear();
	}

	@Test
	public void apiTest_6번() {
		// Given
		List firstMock = mock(List.class);
		List secondMock = mock(List.class);

		// When
		firstMock.add("was called first");
		secondMock.add("was called second");

		// Then
		InOrder inOder = inOrder(firstMock, secondMock);
		inOder.verify(firstMock).add("was called first");
		inOder.verify(secondMock).add("was called second");
	}

	@Test
	public void apiTest_7번() {
		// Given
		List mockOne = mock(List.class);
		List mockTwo = mock(List.class);
		List mockThree = mock(List.class);

		// When
		mockOne.add("one");

		// Then
		verify(mockOne).add("one");
		verify(mockOne, never()).add("two");
		verifyZeroInteractions(mockTwo, mockThree);
	}

	@Test
	public void apiTest_8번() {
		// Given
		List noInteractionMock = mock(List.class);

		// When
		mockList.add("one");
		mockList.add("two");

		// Then
		verify(mockList).add("one");
		verifyNoMoreInteractions(noInteractionMock);
		// verifyNoMoreInteractions보단 never() 사용권장
		verify(noInteractionMock, never()).add(anyString());
	}

	@Test
	public void apiTest_9번() {
		// Given
		when(annotaionMockList.add("1")).thenReturn(true);

		// When
		annotaionMockList.add("1");

		// Then
		// Stubbing을 검증하는 건 의미없음. Mock객체의 행위검증을 위해 추가함
		verify(annotaionMockList).add("1");
	}

	@Test
	public void apiTest_10번() {
		// Given
		when(annotaionMockList.add("1")).thenReturn(true, false, true);

		// When
		System.out.println(annotaionMockList.add("1"));
		System.out.println(annotaionMockList.add("1"));
		System.out.println(annotaionMockList.add("1"));
	}

	@Test
	public void apiTest_11번() {
		// Given
		when(annotaionMockList.get(anyInt())).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				Integer result = (Integer) args[0];
				return Integer.toString(result);
			}
		});

		// When
		String result = (String) annotaionMockList.get(1);

		// Then
		assertThat(result, is("1"));
	}

	@Test
	public void apiTest_12번() {
		// Given
		spyList.add("1");
		// void메서드를 stubbing할때 사용
		// doAnswer(), doThrow(), doReturn()등이 있음
		doNothing().when(spyList).clear();

		// When
		spyList.clear();

		// Then
		assertTrue(spyList.size() != 0);
	}

	@Test
	public void apiTest_12번_doAnswer_thenAnswer() {
		// Given
		String name = "room";
		Room room = new Room(name);
		RoomService roomService = mock(RoomService.class);
		Map<String, Room> roomMap = new HashMap<String, Room>();

		// void메서드를 doAnswer()로 stubbing함
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) {
				Object[] arguments = invocation.getArguments();
				if (arguments != null) {
					Room room = (Room) arguments[0];
					roomMap.put(room.getName(), room);
				}
				return null;
			}
		}).when(roomService).persist(org.mockito.Matchers.any(Room.class));

		when(roomService.findByName(anyString())).thenAnswer(new Answer<Room>() {
			@Override
			public Room answer(InvocationOnMock invocation) {
				Object[] arguments = invocation.getArguments();
				if (arguments != null) {
					String key = (String) arguments[0];
					if (roomMap.containsKey(key)) {
						return roomMap.get(key);
					}
				}
				return null;
			}
		});

		// When
		roomService.persist(room);

		// Then
		assertThat(roomService.findByName(name), equalTo(room));
		assertNull(roomService.findByName("none"));
	}

	public class RoomService {
		public Room findByName(String roomName) {
			return null;
		}

		public void persist(Room room) {
		}
	}

	public class Room {
		String name;

		public Room(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	@Test
	public void apiTest_13번() {
		// Given
		when(spyList.size()).thenReturn(100);
		// spyList에 데이터가 아무것도 없으므로 spyList에 대한 stubbing을 사용할 수 없다.
		// when(spyList.get(0)).thenReturn(1);
		doReturn(100).when(spyList).size();

		// When
		spyList.add(1);
		spyList.add(2);
		System.out.println(spyList.size());

		// Then
		assertThat((Integer) spyList.get(1), is(2));
	}

	// final 메서드는 stubbing하지 못해 에러남!
	@Test
	@Ignore
	public void apiTest_13번_final메서드stubbing() {
		// Given
		FinalMethod method = new FinalMethod();
		FinalMethod spy = spy(method);
		doReturn(3).when(spy).getValue();

		// When
		System.out.println(spy.getValue());
	}

	public class FinalMethod {
		public final int getValue() {
			return 2;
		}
	}

	@Test
	public void apiTest_14번() {
		// Given
		Foo mock = mock(Foo.class, new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) {
				return 5;
			}
		});
		when(mock.getStubValue()).thenReturn(1);

		// When
		System.out.println(mock.getValue(0));

		// Then
		assertThat(mock.getStubValue(), is(1));
	}

	class Foo {
		public int getValue(int a) {
			return 0;
		}

		public int getStubValue() {
			return 2;
		}
	}

	@Test
	public void apiTest_15번() {
		// Given
		ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);

		// When
		personService.savePerson(new Person("test", "홍길동", 20));

		// Then
		verify(personRepository).save(captor.capture());
		Person person = captor.getValue();
		assertThat(person.getId(), is("test12"));
		assertThat(person.getName(), is("변경된이름"));
		assertThat(person.getAge(), is(10));
	}

	@Test
	public void apiTest_19번() {
		// Given
		given(annotaionMockList.get(0)).willReturn("1");

		// When
		annotaionMockList.add("2");

		// Then
		assertThat((String) annotaionMockList.get(0), is("1"));
	}

	@SuppressWarnings("unused")
	@Test
	public void apiTest_20번() {
		// serializable mock
		List serializableMockList = mock(ArrayList.class, Mockito.withSettings().serializable());

		// serializable spying
		List list = new ArrayList();
		List spy = mock(ArrayList.class,
						Mockito.withSettings().spiedInstance(list).defaultAnswer(Mockito.CALLS_REAL_METHODS)
										.serializable());
	}

	@Test
	public void apiTest_22번() {
		// Given
		given(annotaionMockList.get(1)).willReturn(10);

		// When
		annotaionMockList.get(1);

		// Then
		verify(annotaionMockList, timeout(100).times(1)).get(1);
	}

	class PersonService {
		private PersonRepository personRepository = new PersonRepository();

		public void savePerson(Person person) {
			person.changeInformation();
			personRepository.save(person);
		}
	}

	class PersonRepository {
		public void save(Person person) {
		}
	}

	class Person {
		private String id;
		private String name;
		private int age;

		public Person() {
		}

		public Person(String id, String name, int age) {
			this.id = id;
			this.name = name;
			this.age = age;
		}

		public int getAge() {
			return age;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void changeInformation() {
			this.id = "test12";
			this.name = "변경된이름";
			this.age = 10;
		}
	}
}